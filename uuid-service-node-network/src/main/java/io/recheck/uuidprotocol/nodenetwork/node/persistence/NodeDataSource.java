package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.model.*;
import io.recheck.uuidprotocol.domain.node.dto.NodeFindDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.persistence.AuditDataSource;

import java.util.List;
import java.util.Map;

public class NodeDataSource<T extends Node> extends AuditDataSource<T> {

    WrapUnaryEqualToFilter uuidFilter = new WrapUnaryEqualToFilter("uuid");
    WrapUnaryEqualToFilter createdByUuidFilter = new WrapUnaryEqualToFilter("createdBy.userUUID");

    QueryDirection lastUpdatedAtDesc = new QueryDirection("lastUpdatedAt", Query.Direction.DESCENDING);

    InspectableFilter findLastUpdated = new WrapCompositeAndFilter(List.of(uuidFilter), List.of(lastUpdatedAtDesc));

    WrapDTOCompositeAndFilter<NodeFindDTO> wrapNodeFindDTOCompositeAndFilter =
            new WrapDTOCompositeAndFilter<>(NodeFindDTO.class, List.of(createdByUuidFilter), List.of(lastUpdatedAtDesc));

    public NodeDataSource(Class<T> type) {
        super(type);
    }

    public T findLastUpdated(String uuid) {
        Filter filter = findLastUpdated.toFirestoreFilter(Map.of("uuid", uuid));
        Map<String, Query.Direction> orderByLastUpdatedAt = findLastUpdated.getQueryDirectionsMap();
        return whereFindFirst(filter, orderByLastUpdatedAt);
    }

    public List<T> findByDTOAndOrderByLastUpdatedAt(UserDetailsCustom user, NodeFindDTO dto) {
        WrapCompositeAndFilter wrapFilter = wrapNodeFindDTOCompositeAndFilter.getWrapFilter(dto);
        return super.where(
                wrapNodeFindDTOCompositeAndFilter.toFirestoreFilter(wrapFilter, dto, Map.of("createdBy.userUUID", user.getUserUUID())),
                wrapFilter.getQueryDirectionsMap());
    }

    public T softDelete(T existingObject, UserDetailsCustom user) {
        Filter filter = findLastUpdated.toFirestoreFilter(Map.of("uuid", existingObject.getUuid()));
        Map<String, Query.Direction> orderByLastUpdatedAt = findLastUpdated.getQueryDirectionsMap();
        return super.softDeleteAudit(filter, orderByLastUpdatedAt, existingObject, user);
    }

}
