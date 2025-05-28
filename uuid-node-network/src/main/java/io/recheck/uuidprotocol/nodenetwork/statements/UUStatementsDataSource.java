package io.recheck.uuidprotocol.nodenetwork.statements;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.nodenetwork.datasource.AuditDataSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class UUStatementsDataSource extends AuditDataSource<UUStatements> {

    public UUStatementsDataSource() {
        super(UUStatements.class);
    }

    public UUStatements findLastUpdated(UUStatements uuStatements) {
        Filter filter = Filter.and(Filter.equalTo("subject", uuStatements.getSubject()),
                                    Filter.equalTo("predicate", uuStatements.getPredicate()),
                                    Filter.equalTo("object", uuStatements.getObject()));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return whereFindFirst(filter, orderByLastUpdatedAt);
    }

    public Boolean exist(String uuid) {
        Filter filter = Filter.or(Filter.equalTo("subject", uuid), Filter.equalTo("object", uuid));
        return where(filter).stream().findFirst().isPresent();
    }

    @Override
    public UUStatements softDeleteAudit(UUStatements existingObject, String certFingerprint) {
        Instant now = Instant.now();
        existingObject.setSoftDeleted(true);
        existingObject.setSoftDeleteBy(certFingerprint);
        existingObject.setSoftDeletedAt(Instant.now());
        existingObject.setLastUpdatedAt(now);
        existingObject.setLastUpdatedBy(certFingerprint);
        return createOrUpdate(existingObject);
    }
}