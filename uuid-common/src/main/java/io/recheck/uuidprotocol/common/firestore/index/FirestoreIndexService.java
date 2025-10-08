package io.recheck.uuidprotocol.common.firestore.index;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.common.firestore.index.model.*;
import io.recheck.uuidprotocol.common.firestore.model.*;
import io.recheck.uuidprotocol.common.utils.ListUtils;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirestoreIndexService {

    @Data
    static class DataSourceIndexFields {
        private final Class dataSourceType;
        private final List<IndexField> indexFieldList;
    }

    private final FirestoreGoogleApiService firestoreGoogleApiService;
    private final ApplicationContext applicationContext;
    private List<DataSourceIndexFields> indexFieldsList = new ArrayList<>();

    @SneakyThrows
    public void deleteIndexes() {
        log.debug("========================== delete indexes");
        ListIndexes listIndexes = firestoreGoogleApiService.listIndexes();

        log.debug("deleting {} indexes", listIndexes.getIndexes().size());

        for (IndexResponse index : listIndexes.getIndexes()) {
            log.debug("\t deleting {}", index);
            firestoreGoogleApiService.deleteIndex(index.getId());
            log.debug("\t done; wait 5 sec");
            Thread.sleep(1000*5);
        }

        log.debug("========================== END delete indexes");
    }

    @SneakyThrows
    public void initIndexes() {
        log.debug("========================== init indexes");


        log.debug("============== inspect datasources for indexes");
        indexFieldsList = new ArrayList<>();
        Map<String, FirestoreDataSource> beans = applicationContext.getBeansOfType(FirestoreDataSource.class);
        for (Map.Entry<String, FirestoreDataSource> dataSourceEntry : beans.entrySet()) {
            inspectDataSourceForWrapCompositeFilters(dataSourceEntry.getValue());
        }
        log.debug("============== END inspect datasources for indexes {count = {}}", indexFieldsList.size());

        log.debug("============== process firestore api create indexes");
        processIndexes();
        log.debug("============== END process firestore api create indexes");


        log.debug("========================== END init indexes");
    }

    public void processIndexes() {
        int a = indexFieldsList.size() / 4;
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 1. Batch the list
        List<List<DataSourceIndexFields>> batches = ListUtils.batches(indexFieldsList, a);

        // 2. Create tasks for each batch
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < batches.size(); i++) {
            int finalI = i;
            tasks.add(() -> {
                log.debug("starting batch = {}", finalI);
                for (DataSourceIndexFields dataSourceIndexFields : batches.get(finalI)) {
                    createIndex(dataSourceIndexFields);
                }
                return null;
            });
        }

        // 3. Execute concurrently and wait
        try {
            List<Future<Void>> futures = executor.invokeAll(tasks);
            log.debug("futures batch = {}", futures.size());
            // Wait for all to complete (and rethrow if any failed)
            for (Future<Void> f : futures) {
                f.get();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch processing interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error during batch processing", e.getCause());
        }
    }


    private void inspectDataSourceForWrapCompositeFilters(FirestoreDataSource dataSource) {
        log.debug("FirestoreDataSource entry = {}", dataSource.getClass().getSimpleName());

        Map<Field, WrapCompositeAndFilter> fieldInspectableFilterMap = ReflectionUtils.getFieldsOfType(dataSource, WrapCompositeAndFilter.class);
        for (Map.Entry<Field, WrapCompositeAndFilter> fieldInspectableFilterEntry : fieldInspectableFilterMap.entrySet()) {
            log.debug("\t WrapCompositeAndFilter Field.name = {}", fieldInspectableFilterEntry.getKey().getName());
            logWrapCompositeAndFilter(fieldInspectableFilterEntry.getValue());
            List<IndexField> indexFields = toIndexFields(fieldInspectableFilterEntry.getValue());
            if (indexFields.size() > 1) {
                indexFieldsList.add(new DataSourceIndexFields(dataSource.getCollectionType(), indexFields));
            }
        }

        Map<Field, WrapDTOCompositeAndFilter> fieldWrapDTOCompositeAndFilterMap = ReflectionUtils.getFieldsOfType(dataSource, WrapDTOCompositeAndFilter.class);
        for (Map.Entry<Field, WrapDTOCompositeAndFilter> fieldWrapDTOCompositeAndFilterEntry : fieldWrapDTOCompositeAndFilterMap.entrySet()) {
            log.debug("\t WrapDTOCompositeAndFilter Field.name = {}", fieldWrapDTOCompositeAndFilterEntry.getKey().getName());
            Map<String, WrapCompositeAndFilter> inspectableFilterMap = fieldWrapDTOCompositeAndFilterEntry.getValue().getInspectableFilterMap();
            for (WrapCompositeAndFilter wrapCompositeAndFilter : inspectableFilterMap.values()) {
                logWrapCompositeAndFilter(wrapCompositeAndFilter);
                List<IndexField> indexFields = toIndexFields(wrapCompositeAndFilter);
                if (indexFields.size() > 1) {
                    indexFieldsList.add(new DataSourceIndexFields(dataSource.getCollectionType(), indexFields));
                }
            }
        }
    }

    private void logWrapCompositeAndFilter(WrapCompositeAndFilter wrapCompositeAndFilter) {
        log.debug("\t\t WrapCompositeAndFilter = ");
        for (WrapUnaryFilter filter : wrapCompositeAndFilter.getFilters()) {
            log.debug("\t\t\t filter = {}", filter.getField());
        }
        log.debug("\t\t\t queryDirectionsMap = {}", wrapCompositeAndFilter.getQueryDirectionsMap());
    }

    private List<IndexField> toIndexFields(WrapCompositeAndFilter wrapCompositeAndFilter) {
        List<IndexField> indexFields = new ArrayList<>();

        if (wrapCompositeAndFilter.getFilters() != null && !wrapCompositeAndFilter.getFilters().isEmpty()) {
            indexFields.addAll(wrapCompositeAndFilter.getFilters().stream()
                    .map(wrapUnaryFilter -> new IndexField(wrapUnaryFilter.getField())).toList());
        }

        if (wrapCompositeAndFilter.getQueryDirections() != null && !wrapCompositeAndFilter.getQueryDirections().isEmpty()) {
            indexFields.addAll(wrapCompositeAndFilter.getQueryDirections().stream()
                    .map(queryDirection -> new IndexField(queryDirection.getField(), queryDirection.getDirection())).toList());
        }

        return indexFields;
    }



    @SneakyThrows
    private void createIndex(DataSourceIndexFields dataSourceIndexFields) {
        List<IndexField> indexFields = dataSourceIndexFields.getIndexFieldList();
        Class collectionType = dataSourceIndexFields.getDataSourceType();

        log.debug("\t\t\t\tcreate index for collectionType = {} ; index = {} ", collectionType.getSimpleName(), indexFields);

        if (indexFields != null && indexFields.size() < 2) {
            log.debug("\t\t\t\t not enough indexes for compound index creation");
            return;
        }

        Operation operation = firestoreGoogleApiService.createIndex(collectionType.getSimpleName(), indexFields);

        log.debug("\t\t\t\t\t operation = {}", operation);

        if (operation.getError() == null &&
                operation.getMetadata().getState().equals(State.INITIALIZING) &&
                StringUtils.hasText(operation.getMetadata().getId()) &&
                operation.getMetadata().getCollection().equals(collectionType.getSimpleName())) {
            while (true) {
                log.debug("\t\t\t\t\t check index state after 33 sec");
                Thread.sleep(1000*33);
                IndexResponse index = firestoreGoogleApiService.getIndex(operation.getMetadata().getId());
                if (index.getState().equals(State.READY)) {
                    log.debug("\t\t\t\t\t index ready = {}", index);
                    break;
                }
                log.debug("\t\t\t\t\t index = {}", index);
            }
        }
    }

}
