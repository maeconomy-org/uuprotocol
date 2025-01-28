package io.recheck.uuidprotocol.domain.node.datasource;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.datasource.AbstractFirestoreDataSource;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.model.audit.Audit;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.Optional;

public class AuditDataSource<T extends Audit> extends AbstractFirestoreDataSource<T> {

    public AuditDataSource(Class<T> type) {
        super(type);
    }

    public T createOrUpdateAudit(T pojoAudit, String ownerCertFingerprint) {
        String documentId = getId(pojoAudit);
        Instant now = Instant.now();
        T existingObject = findByDocumentId(documentId);
        if (existingObject == null) {
            pojoAudit.setCreatedAt(now);
            pojoAudit.setCreatedBy(ownerCertFingerprint);
        }
        else {
            pojoAudit.setCreatedAt(existingObject.getCreatedAt());
            pojoAudit.setCreatedBy(existingObject.getCreatedBy());
            pojoAudit.setSoftDeletedAt(existingObject.getSoftDeletedAt());
            pojoAudit.setSoftDeleteBy(existingObject.getSoftDeleteBy());
            pojoAudit.setSoftDeleted(existingObject.getSoftDeleted());
        }

        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(ownerCertFingerprint);
        return createOrUpdate(pojoAudit);
    }

    @SneakyThrows
    public T softDeleteAudit(String documentId, String ownerCertFingerprint) {
        T existingObject = findByDocumentId(documentId);
        if (existingObject == null) {
            throw new NotFoundException("Not found for soft delete");
        }

        if (!existingObject.getSoftDeleted()) {
            existingObject.setSoftDeleted(true);
            existingObject.setSoftDeleteBy(ownerCertFingerprint);
            existingObject.setSoftDeletedAt(Instant.now());
        }

        return createOrUpdate(existingObject);
    }

    public T findByUUIDAndSoftDeletedFalse(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", false));
        Optional<T> firstNodeOptional = where(filter).stream().findFirst();
        return firstNodeOptional.orElse(null);
    }


}
