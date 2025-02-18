package io.recheck.uuidprotocol.common.datasource;

import com.google.cloud.firestore.*;
import io.recheck.uuidprotocol.common.model.FirestoreId;
import io.recheck.uuidprotocol.common.model.QueryCompositeFilter;
import io.recheck.uuidprotocol.common.utils.ListUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class AbstractTypeFirestoreDataSource<T_COLLECTION> {

    @Autowired
    private Firestore firestore;

    @Getter
    private final Class<T_COLLECTION> type;

    public T_COLLECTION createOrUpdate(T_COLLECTION pojo) {
        CollectionReference collectionReference = firestore.collection(type.getSimpleName());
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

    public <T_CAST> T_CAST findByDocumentId(String documentId, Class<T_CAST> castType) {
        List<T_CAST> result = whereEqualTo(FieldPath.documentId(), documentId, castType);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public <T_CAST> List<T_CAST> findByDocumentId(List<String> documentIds, Class<T_CAST> castType) {
        return whereIn(FieldPath.documentId(), documentIds, castType);
    }

    public <T_CAST> List<T_CAST> findAll(Class<T_CAST> castType) {
        return documentReferenceToObjects(firestore.collection(type.getSimpleName()).listDocuments(), castType);
    }

    public <T_CAST> List<T_CAST> whereEqualTo(String field, Object value, Class<T_CAST> castType) {
        return whereEqualTo(FieldPath.of(field), value, castType);
    }

    @SneakyThrows
    public <T_CAST> List<T_CAST> whereEqualTo(FieldPath fieldPath, Object value, Class<T_CAST> castType) {
        if (value != null) {
            if (value instanceof String sValue) {
                if (!StringUtils.hasText(sValue)) {
                    return new ArrayList<>();
                }
            }
            return documentSnapshotToObjects(firestore.collection(type.getSimpleName()).whereEqualTo(fieldPath, value).get().get().getDocuments(), castType);
        }
        return new ArrayList<>();
    }

    public <T_CAST> List<T_CAST> whereIn(String field, List<? extends Object> values, Class<T_CAST> castType) {
        return whereIn(FieldPath.of(field), values, castType);
    }

    public <T_CAST> List<T_CAST> whereIn(FieldPath fieldPath, List<? extends Object> values, Class<T_CAST> castType) {
        List<QueryDocumentSnapshot> queryDocumentSnapshots = new ArrayList<>();
        if (!values.isEmpty()) {
            Stream<List<? extends Object>> documentIdsBatches = ListUtils.batches(values, 29);
            documentIdsBatches.forEach(new Consumer<>() {
                @Override
                @SneakyThrows
                public void accept(List<? extends Object> batch) {
                    queryDocumentSnapshots.addAll(firestore.collection(type.getSimpleName()).whereIn(fieldPath, batch).get().get().getDocuments());
                }
            });
        }
        return documentSnapshotToObjects(queryDocumentSnapshots, castType);
    }

    public <T_CAST> List<T_CAST> where(QueryCompositeFilter queryCompositeFilter, Class<T_CAST> castType) {
        return where(QueryBuilder.build(queryCompositeFilter), castType);
    }

    @SneakyThrows
    public <T_CAST> List<T_CAST> where(Filter filter, Class<T_CAST> castType) {
        return documentSnapshotToObjects(firestore.collection(type.getSimpleName()).where(filter).get().get().getDocuments(), castType);
    }

    protected <T_CAST> List<T_CAST> documentSnapshotToObjects(Iterable<QueryDocumentSnapshot> queryDocumentSnapshots, Class<T_CAST> castType) {
        ArrayList<T_CAST> results = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
            results.add(toObject(queryDocumentSnapshot.getReference(), castType));
        }
        return results;
    }

    protected <T_CAST> List<T_CAST> documentReferenceToObjects(Iterable<DocumentReference> documentReferences, Class<T_CAST> castType) {
        ArrayList<T_CAST> results = new ArrayList<>();
        for (DocumentReference documentReference : documentReferences) {
            results.add(toObject(documentReference, castType));
        }
        return results;
    }

    @SneakyThrows
    protected <T_CAST> T_CAST toObject(DocumentReference documentReference, Class<T_CAST> castType) {
        DocumentSnapshot document = documentReference.get().get();
        if(document.exists()) {
            return document.toObject(castType);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    protected String getId(T_COLLECTION object) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(FirestoreId.class)) {
                return (String) field.get(object);
            }
        }
        return null;
    }

    @SneakyThrows
    protected void setId(T_COLLECTION object, String id) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(FirestoreId.class)) {
                field.set(object, id);
            }
        }
    }

}
