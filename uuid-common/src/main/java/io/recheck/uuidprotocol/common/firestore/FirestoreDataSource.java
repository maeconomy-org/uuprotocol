package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.*;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class FirestoreDataSource<T_COLLECTION> {

    @Autowired
    protected Firestore firestore;

    @Getter
    protected final Class<T_COLLECTION> collectionType;

    protected CollectionReference getCollection() {
        return firestore.collection(collectionType.getSimpleName());
    }

    @SneakyThrows
    public T_COLLECTION create(T_COLLECTION pojo) {
        CollectionReference collectionReference = getCollection();
        collectionReference.document().set(pojo).get();
        return pojo;
    }

    @SneakyThrows
    public void update(Filter filter, String field, Object value) {
        CollectionReference collectionReference = getCollection();
        List<QueryDocumentSnapshot> queryDocumentSnapshots = collectionReference.where(filter).get().get().getDocuments();
        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
            if (queryDocumentSnapshot.exists()) {
                queryDocumentSnapshot.getReference().update(field, value);
            }
        }
    }

    @SneakyThrows
    public List<T_COLLECTION> where(Object filterCriteria, Map<String, Query.Direction> orderByFields) {
        List<Filter> filters = new ArrayList<>();

        List<Field> allFields = ReflectionUtils.getAllFields(filterCriteria.getClass());
        for (Field field : allFields) {
            Object value = field.get(filterCriteria);
            if (value != null) { //filter by all fields that has non empty value
                if (value instanceof String) {
                    if (!StringUtils.hasText((CharSequence) value)) {
                        continue;
                    }
                }
                filters.add(Filter.equalTo(field.getName(), value));
            }
        }
        return where(Filter.and(filters.toArray(new Filter[0])), orderByFields);
    }

    public T_COLLECTION whereFindFirst(Filter filter) {
        return where(filter, null).stream().findFirst().orElse(null);
    }

    public List<T_COLLECTION> where(Filter filter) {
        return where(filter, null);
    }

    public T_COLLECTION whereFindFirst(Filter filter, Map<String, Query.Direction> orderByFields) {
        return where(filter, orderByFields).stream().findFirst().orElse(null);
    }

    public List<T_COLLECTION> where(Filter filter, Map<String, Query.Direction> orderByFields) {
        return documentSnapshotToObjects(getDocuments(filter, orderByFields));
    }




    @SneakyThrows
    private List<QueryDocumentSnapshot> getDocuments(Filter filter, Map<String, Query.Direction> orderByFields) {
        return getWhereQuery(filter, orderByFields).get().get().getDocuments();
    }

    @SneakyThrows
    private Query getWhereQuery(Filter filter, Map<String, Query.Direction> orderByFields) {
        return getOrderByQuery(orderByFields).where(filter);
    }

    private Query getOrderByQuery(Map<String, Query.Direction> orderByFields) {
        CollectionReference collection = getCollection();

        if (orderByFields == null || orderByFields.isEmpty()) {
            return collection;
        }

        Query query = collection;
        for (Map.Entry<String, Query.Direction> entry : orderByFields.entrySet()) {
            if (entry.getValue() != null) {
                query = collection.orderBy(FieldPath.of(entry.getKey()), entry.getValue());
            }
            else {
                query = collection.orderBy(entry.getKey());
            }
        }

        return query;
    }

    private List<T_COLLECTION> documentSnapshotToObjects(Iterable<QueryDocumentSnapshot> queryDocumentSnapshots) {
        ArrayList<T_COLLECTION> results = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
            results.add(toObject(queryDocumentSnapshot));
        }
        return results;
    }

    @SneakyThrows
    private T_COLLECTION toObject(QueryDocumentSnapshot queryDocumentSnapshot) {
        if(queryDocumentSnapshot.exists()) {
            return queryDocumentSnapshot.toObject(collectionType);
        }
        else {
            return null;
        }
    }

}
