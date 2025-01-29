package io.recheck.uuidprotocol.domain.node.datasource;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.datasource.AbstractFirestoreDataSource;
import io.recheck.uuidprotocol.domain.node.model.audit.Audit;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    public T softDeleteAudit(T existingObject, String ownerCertFingerprint) {
        if (!existingObject.getSoftDeleted()) {
            existingObject.setSoftDeleted(true);
            existingObject.setSoftDeleteBy(ownerCertFingerprint);
            existingObject.setSoftDeletedAt(Instant.now());
        }

        return createOrUpdate(existingObject);
    }

    public List<T> findByOrFindAll(String createdBy, Boolean softDeleted) {
        return whereAndFiltersOrFindAll(filtersBy(createdBy, softDeleted));
    }

    protected List<Filter> filtersBy(String createdBy, Boolean softDeleted) {
        List<Filter> filters = new ArrayList<>();
        if (StringUtils.hasText(createdBy)) {
            filters.add(Filter.equalTo("createdBy", createdBy));
        }
        if (softDeleted != null) {
            filters.add(Filter.equalTo("softDeleted", softDeleted));
        }
        return filters;
    }


}
