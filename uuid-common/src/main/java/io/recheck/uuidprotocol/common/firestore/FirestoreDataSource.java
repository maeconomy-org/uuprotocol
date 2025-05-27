package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.*;
import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import io.recheck.uuidprotocol.common.firestore.querybuilder.QueryBuilder;
import io.recheck.uuidprotocol.common.firestore.querybuilder.model.QueryCompositeFilter;
import io.recheck.uuidprotocol.common.utils.ListUtils;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
        documentReference.set(pojo);
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





    public T_COLLECTION findByDocumentId(String documentId, Map<String, Query.Direction> orderByFields) {
        List<T_COLLECTION> result = whereEqualTo(FieldPath.documentId(), documentId, orderByFields);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public List<T_COLLECTION> findAll() {
        return documentReferenceToObjects(listDocuments());
    }

    public List<T_COLLECTION> whereEqualTo(String field, Object value) {
        return whereEqualTo(FieldPath.of(field), value, null);
    }

    @SneakyThrows
    public List<T_COLLECTION> whereEqualTo(FieldPath fieldPath, Object value, Map<String, Query.Direction> orderByFields) {
        return documentSnapshotToObjects(getOrderByQuery(orderByFields).whereEqualTo(fieldPath, value).get().get().getDocuments());
    }

    @SneakyThrows
    public List<T_COLLECTION> whereIn(FieldPath fieldPath, List<? extends Object> values, Map<String, Query.Direction> orderByFields) {
        List<QueryDocumentSnapshot> queryDocumentSnapshots = new ArrayList<>();
        if (!values.isEmpty()) {
            List<List<? extends Object>> documentIdsBatches = ListUtils.batches(values, 29);
            for (List<?> batch : documentIdsBatches) {
                queryDocumentSnapshots.addAll(getOrderByQuery(orderByFields).whereIn(fieldPath, batch).get().get().getDocuments());
            }
        }
        return documentSnapshotToObjects(queryDocumentSnapshots);
    }



    public List<T_COLLECTION> where(QueryCompositeFilter queryCompositeFilter) {
        return where(QueryBuilder.build(queryCompositeFilter), queryCompositeFilter.getOrderByFields());
    }

    public List<T_COLLECTION> where(Filter filter) {
        return documentSnapshotToObjects(getDocuments(filter, null));
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

    private Iterable<DocumentReference> listDocuments() {
        return getCollection().listDocuments();
    }

    private List<T_COLLECTION> documentSnapshotToObjects(Iterable<QueryDocumentSnapshot> queryDocumentSnapshots) {
        ArrayList<T_COLLECTION> results = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
            results.add(toObject(queryDocumentSnapshot.getReference()));
        }
        return results;
    }

    private List<T_COLLECTION> documentReferenceToObjects(Iterable<DocumentReference> documentReferences) {
        ArrayList<T_COLLECTION> results = new ArrayList<>();
        for (DocumentReference documentReference : documentReferences) {
            results.add(toObject(documentReference));
        }
        return results;
    }

    @SneakyThrows
    private T_COLLECTION toObject(DocumentReference documentReference) {
        DocumentSnapshot document = documentReference.get().get();
        if(document.exists()) {
            return document.toObject(collectionType);
        }
        else {
            return null;
        }
    }

}
