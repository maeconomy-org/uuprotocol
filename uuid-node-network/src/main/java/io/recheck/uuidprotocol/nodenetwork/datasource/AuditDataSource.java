package io.recheck.uuidprotocol.nodenetwork.datasource;

import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.domain.node.model.audit.Audit;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AuditDataSource<T extends Audit> extends FirestoreDataSource<T> {

    public AuditDataSource(Class<T> type) {
        super(type);
    }

    public T createOrUpdateAudit(T pojoAudit, String certFingerprint) {
        String documentId = getId(pojoAudit);
        Instant now = Instant.now();
        T existingObject = findByDocumentId(documentId, Map.of("lastUpdatedAt", Query.Direction.DESCENDING));
        if (existingObject == null) {
            pojoAudit.setCreatedAt(now);
            pojoAudit.setCreatedBy(certFingerprint);
        }
        else {
            pojoAudit.setCreatedAt(existingObject.getCreatedAt());
            pojoAudit.setCreatedBy(existingObject.getCreatedBy());
            pojoAudit.setSoftDeletedAt(existingObject.getSoftDeletedAt());
            pojoAudit.setSoftDeleteBy(existingObject.getSoftDeleteBy());
            pojoAudit.setSoftDeleted(existingObject.getSoftDeleted());
        }

        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(certFingerprint);
        return createOrUpdate(pojoAudit);
    }

    public T softDeleteAudit(T existingObject, String certFingerprint) {
        if (!existingObject.getSoftDeleted()) {
            existingObject.setSoftDeleted(true);
            existingObject.setSoftDeleteBy(certFingerprint);
            existingObject.setSoftDeletedAt(Instant.now());
        }

        return createOrUpdate(existingObject);
    }

    public List<T> where(Object pojo) {
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return super.where(pojo, orderByLastUpdatedAt);
    }
}
