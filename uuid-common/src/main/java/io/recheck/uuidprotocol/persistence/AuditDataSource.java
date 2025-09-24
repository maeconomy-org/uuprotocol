package io.recheck.uuidprotocol.persistence;

import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.domain.audit.Audit;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;

import java.time.Instant;
import java.util.List;
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

    public T softDeleteAudit(T existingObject, UserDetailsCustom user) {
        existingObject.setSoftDeleted(true);
        existingObject.setSoftDeleteBy(new AuditUser(user));
        existingObject.setSoftDeletedAt(Instant.now());
        return create(existingObject);
    }

    public List<T> findByDTOAndOrderByLastUpdatedAt(UserDetailsCustom user, Object dto) {
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return super.whereCreatedByUserUUID(new AuditUser(user), dto, orderByLastUpdatedAt);
    }
}
