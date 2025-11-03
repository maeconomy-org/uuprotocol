package io.recheck.uuidprotocol.common.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

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
    public T_COLLECTION updateFirst(Filter filter, T_COLLECTION pojo) {
        return updateFirst(filter, null, pojo);
    }

    @SneakyThrows
    public T_COLLECTION updateFirst(Filter filter, Map<String, Query.Direction> orderByFields, T_COLLECTION pojo) {
        QueryDocumentSnapshot queryDocumentSnapshot = getDocuments(getWhereQuery(filter, orderByFields))
                .stream().findFirst().orElse(null);

        if (queryDocumentSnapshot != null && queryDocumentSnapshot.exists()) {
            queryDocumentSnapshot.getReference().set(pojo).get();
            return pojo;
        }

        return null;
    }



    public List<T_COLLECTION> where(Filter filter) {
        return where(filter, null);
    }

    public List<T_COLLECTION> where(Object filterCriteria, Map<String, Query.Direction> orderByFields) {
        List<Filter> filters = FirestoreUtils.toFirestoreFilters(filterCriteria);
        return where(Filter.and(filters.toArray(new Filter[0])), orderByFields);
    }

    public T_COLLECTION whereFindFirst(Filter filter) {
        return where(filter, null).stream().findFirst().orElse(null);
    }

    public T_COLLECTION whereFindFirst(Filter filter, Map<String, Query.Direction> orderByFields) {
        return where(filter, orderByFields).stream().findFirst().orElse(null);
    }

    public List<T_COLLECTION> where(Filter filter, Map<String, Query.Direction> orderByFields) {
        return documentSnapshotToObjects(getDocuments(getWhereQuery(filter, orderByFields)));
    }

    public List<T_COLLECTION> whereArrayContains(String field, Object value) {
        CollectionReference collection = getCollection();
        Query query = collection.whereArrayContains(field, value);
        return documentSnapshotToObjects(getDocuments(query));
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

    @SneakyThrows
    private List<QueryDocumentSnapshot> getDocuments(Query query) {
        return query.get().get().getDocuments();
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
