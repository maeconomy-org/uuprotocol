package io.recheck.uuidprotocol.persistence;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.domain.audit.Audit;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;

import java.time.Instant;
import java.util.Map;

public class AuditDataSource<T extends Audit> extends FirestoreDataSource<T> {

    public AuditDataSource(Class<T> type) {
        super(type);
    }

    public T createAudit(T pojoAudit, UserDetailsCustom user) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(new AuditUser(user));
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(new AuditUser(user));
        return create(pojoAudit);
    }

    public T softDeleteAudit(Filter filter, Map<String, Query.Direction> orderByFields, T existingObject, UserDetailsCustom user) {
        Instant now = Instant.now();
        existingObject.setLastUpdatedAt(now);
        existingObject.setLastUpdatedBy(new AuditUser(user));

        existingObject.setSoftDeleted(true);
        existingObject.setSoftDeletedAt(now);
        existingObject.setSoftDeleteBy(new AuditUser(user));

        return updateFirst(filter, orderByFields, existingObject);
    }

}
