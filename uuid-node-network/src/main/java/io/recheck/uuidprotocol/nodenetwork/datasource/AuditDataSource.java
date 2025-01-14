package io.recheck.uuidprotocol.nodenetwork.datasource;

import io.recheck.uuidprotocol.common.datasource.AbstractFirestoreDataSource;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.nodenetwork.model.audit.AuditCommon;
import lombok.SneakyThrows;

import java.time.Instant;

public class AuditDataSource<T extends AuditCommon> extends AbstractFirestoreDataSource<T> {

    public AuditDataSource(Class<T> type) {
        super(type);
    }

    public T createOrUpdateAuditCommon(T pojoAuditCommon, String clientCertFingerprint) {
        String documentId = getId(pojoAuditCommon);
        Instant now = Instant.now();
        T existingObject = findByDocumentId(documentId);
        if (existingObject == null) {
            pojoAuditCommon.setCreatedAt(now);
            pojoAuditCommon.setCreatedBy(clientCertFingerprint);
        }
        else {
            pojoAuditCommon.setCreatedAt(existingObject.getCreatedAt());
            pojoAuditCommon.setCreatedBy(existingObject.getCreatedBy());
            pojoAuditCommon.setSoftDeletedAt(existingObject.getSoftDeletedAt());
            pojoAuditCommon.setSoftDeleteBy(pojoAuditCommon.getSoftDeleteBy());
            pojoAuditCommon.setSoftDeleted(pojoAuditCommon.getSoftDeleted());
        }

        pojoAuditCommon.setLastUpdatedAt(now);
        pojoAuditCommon.setLastUpdatedBy(clientCertFingerprint);
        return createOrUpdate(pojoAuditCommon);
    }

    @SneakyThrows
    public T softDeleteAuditCommon(String documentId, String clientCertFingerprint) {
        T existingObject = findByDocumentId(documentId);
        if (existingObject == null) {
            throw new NotFoundException("Object not found for soft delete");
        }

        if (Boolean.FALSE.equals(existingObject.getSoftDeleted())) {
            existingObject.setSoftDeleted(true);
            existingObject.setSoftDeleteBy(clientCertFingerprint);
            existingObject.setSoftDeletedAt(Instant.now());
        }

        return createOrUpdate(existingObject);
    }


}
