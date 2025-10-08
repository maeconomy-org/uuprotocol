package io.recheck.uuidprotocol.nodenetwork.statements;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.model.*;
import io.recheck.uuidprotocol.domain.statements.dto.UUStatementFindDTO;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.persistence.AuditDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UUStatementsDataSource extends AuditDataSource<UUStatements> {

    WrapUnaryEqualToFilter subjectFilter = new WrapUnaryEqualToFilter("subject");
    WrapUnaryEqualToFilter objectFilter = new WrapUnaryEqualToFilter("object");
    WrapUnaryEqualToFilter predicateFilter = new WrapUnaryEqualToFilter("predicate");
    WrapUnaryEqualToFilter createdByUuidFilter = new WrapUnaryEqualToFilter("createdBy.userUUID");

    QueryDirection lastUpdatedAtDesc = new QueryDirection("lastUpdatedAt", Query.Direction.DESCENDING);

    InspectableFilter existBySubjectAndObject = new WrapCompositeOrFilter(List.of(subjectFilter, objectFilter));
    InspectableFilter findLastUpdated = new WrapCompositeAndFilter(List.of(subjectFilter, predicateFilter, objectFilter), List.of(lastUpdatedAtDesc));

    WrapDTOCompositeAndFilter<UUStatementFindDTO> wrapUUStatementFindDTOCompositeAndFilter =
            new WrapDTOCompositeAndFilter<>(UUStatementFindDTO.class, List.of(createdByUuidFilter), List.of(lastUpdatedAtDesc));

    public UUStatementsDataSource() {
        super(UUStatements.class);
    }

    public UUStatements findLastUpdated(UUStatements uuStatements) {
        Filter findFilter = findLastUpdated.toFirestoreFilter(Map.of(
                "subject", uuStatements.getSubject(),
                "predicate", uuStatements.getPredicate(),
                "object", uuStatements.getObject()));
        Map<String, Query.Direction> lastUpdatedAtDescDirection = findLastUpdated.getQueryDirectionsMap();
        return whereFindFirst(findFilter, lastUpdatedAtDescDirection);
    }

    public Boolean exist(String uuid) {
        return whereFindFirst(existBySubjectAndObject.toFirestoreFilter(Map.of("subject", uuid, "object", uuid))) != null;
    }

    public List<UUStatements> findByDTOAndOrderByLastUpdatedAt(UserDetailsCustom user, UUStatementFindDTO dto) {
        WrapCompositeAndFilter wrapFilter = wrapUUStatementFindDTOCompositeAndFilter.getWrapFilter(dto);
        return super.where(
                wrapUUStatementFindDTOCompositeAndFilter.toFirestoreFilter(wrapFilter, dto, Map.of("createdBy.userUUID", user.getUserUUID())),
                wrapFilter.getQueryDirectionsMap());
    }

    public UUStatements softDelete(UUStatements existingObject, UserDetailsCustom user) {
        Filter findFilter = findLastUpdated.toFirestoreFilter(Map.of(
                "subject", existingObject.getSubject(),
                "predicate", existingObject.getPredicate(),
                "object", existingObject.getObject()));
        Map<String, Query.Direction> lastUpdatedAtDescDirection = findLastUpdated.getQueryDirectionsMap();
        return super.softDeleteAudit(findFilter, lastUpdatedAtDescDirection, existingObject, user);
    }

}