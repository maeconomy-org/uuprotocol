package io.recheck.uuidprotocol.nodenetwork.statements;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.FirestoreUtils;
import io.recheck.uuidprotocol.domain.statements.dto.UUStatementFindDTO;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.persistence.AuditDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<UUStatements> findByDTOAndOrderByLastUpdatedAt(UserDetailsCustom user, UUStatementFindDTO dto) {
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        List<Filter> filters = FirestoreUtils.getFilters(dto);
        filters.add(Filter.equalTo("createdBy.userUUID", user.getUserUUID()));
        return super.where(Filter.and(filters.toArray(new Filter[0])), orderByLastUpdatedAt);
    }

    public UUStatements softDelete(UUStatements existingObject, UserDetailsCustom user) {
        Filter filter = Filter.and(Filter.equalTo("subject", existingObject.getSubject()),
                Filter.equalTo("predicate", existingObject.getPredicate()),
                Filter.equalTo("object", existingObject.getObject()));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return super.softDeleteAudit(filter, orderByLastUpdatedAt, existingObject, user);
    }

}