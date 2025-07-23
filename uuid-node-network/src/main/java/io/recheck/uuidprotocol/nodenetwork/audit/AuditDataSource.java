package io.recheck.uuidprotocol.nodenetwork.audit;

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

    public T createAudit(T pojoAudit, String certFingerprint) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(certFingerprint);
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(certFingerprint);
        return createOrUpdate(pojoAudit);
    }

    public T softDeleteAudit(T existingObject, String certFingerprint) {
        existingObject.setSoftDeleted(true);
        existingObject.setSoftDeleteBy(certFingerprint);
        existingObject.setSoftDeletedAt(Instant.now());
        return createOrUpdate(existingObject);
    }

    public List<T> findByDTOAndOrderByLastUpdatedAt(Object dto) {
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return super.where(dto, orderByLastUpdatedAt);
    }
}
