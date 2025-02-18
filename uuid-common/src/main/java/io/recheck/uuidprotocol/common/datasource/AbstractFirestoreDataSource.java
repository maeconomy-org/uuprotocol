package io.recheck.uuidprotocol.common.datasource;

import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.model.QueryCompositeFilter;

import java.util.List;

public abstract class AbstractFirestoreDataSource<T_COLLECTION> extends AbstractTypeFirestoreDataSource<T_COLLECTION> {

    public AbstractFirestoreDataSource(Class<T_COLLECTION> type) {
        super(type);
    }

    public T_COLLECTION findByDocumentId(String documentId) {
        return super.findByDocumentId(documentId, super.getType());
    }

    public List<T_COLLECTION> findByDocumentId(List<String> documentIds) {
        return super.findByDocumentId(documentIds, super.getType());
    }

    public List<T_COLLECTION> findAll() {
        return super.findAll(super.getType());
    }

    public List<T_COLLECTION> whereEqualTo(String field, Object value) {
        return super.whereEqualTo(field, value, super.getType());
    }

    public List<T_COLLECTION> whereEqualTo(FieldPath fieldPath, Object value) {
        return super.whereEqualTo(fieldPath, value, super.getType());
    }

    public List<T_COLLECTION> whereIn(String field, List<? extends Object> values) {
        return super.whereIn(field, values, super.getType());
    }

    public List<T_COLLECTION> whereIn(FieldPath fieldPath, List<? extends Object> values) {
        return super.whereIn(fieldPath, values, super.getType());
    }

    public List<T_COLLECTION> where(QueryCompositeFilter queryCompositeFilter) {
        return super.where(queryCompositeFilter, super.getType());
    }

    public List<T_COLLECTION> where(Filter filter) {
        return super.where(filter, super.getType());
    }


}
