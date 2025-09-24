package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.*;
import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
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
    public T_COLLECTION createOrUpdate(T_COLLECTION pojo) {
        CollectionReference collectionReference = getCollection();
        DocumentReference documentReference;
        String documentId = getId(pojo);
        if (StringUtils.hasText(documentId)) {
            documentReference = collectionReference.document(documentId);
        }
        else {
            documentReference = collectionReference.document();
            setId(pojo, documentReference.getId());
        }
        documentReference.set(pojo).get();
        return pojo;
    }

    @SneakyThrows
    public String getId(Object object) {
        return ReflectionUtils.getValueAnnotationPresent(FirestoreId.class, object);
    }

    @SneakyThrows
    private void setId(Object object, String id) {
        ReflectionUtils.setValueAnnotationPresent(FirestoreId.class, object, id);
    }

    @SneakyThrows
    public List<T_COLLECTION> whereCreatedByUserUUID(AuditUser user, Object filterCriteria, Map<String, Query.Direction> orderByFields) {
        List<Filter> filters = getFilters(filterCriteria);
        filters.add(Filter.equalTo("createdBy.userUUID", user.getUserUUID()));
        return where(Filter.and(filters.toArray(new Filter[0])), orderByFields);
    }

    @SneakyThrows
    public List<T_COLLECTION> where(Object filterCriteria, Map<String, Query.Direction> orderByFields) {
        List<Filter> filters = getFilters(filterCriteria);
        return where(Filter.and(filters.toArray(new Filter[0])), orderByFields);
    }

    @SneakyThrows
    private List<Filter> getFilters(Object filterCriteria) {
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

        return filters;
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
    public List<T_COLLECTION> whereArrayContains(String field, Object value) {
        CollectionReference collection = getCollection();
        return documentSnapshotToObjects(collection.whereArrayContains(field, value).get().get().getDocuments());
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
