package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.FirestoreUtils;
import io.recheck.uuidprotocol.domain.node.dto.NodeFindDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.persistence.AuditDataSource;

import java.util.List;
import java.util.Map;

public class NodeDataSource<T extends Node> extends AuditDataSource<T> {

    public NodeDataSource(Class<T> type) {
        super(type);
    }

    public T findLastUpdated(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", false));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        return whereFindFirst(filter, orderByLastUpdatedAt);
    }

    public T findLastDeleted(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", true));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("softDeletedAt", Query.Direction.DESCENDING);
        return whereFindFirst(filter, orderByLastUpdatedAt);
    }

    public T findLast(String uuid) {
        T lastUpdated = findLastUpdated(uuid);
        T lastDeleted = findLastDeleted(uuid);

        if (lastUpdated != null) {
            if (lastDeleted == null || lastUpdated.getLastUpdatedAt().isAfter(lastDeleted.getSoftDeletedAt())) {
                return lastUpdated;
            }
        }

        return lastDeleted;
    }

    public List<T> findByDTOAndOrderByLastUpdatedAt(UserDetailsCustom user, NodeFindDTO dto) {
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        List<Filter> filters = FirestoreUtils.getFilters(dto);
        filters.add(Filter.equalTo("createdBy.userUUID", user.getUserUUID()));
        return super.where(Filter.and(filters.toArray(new Filter[0])), orderByLastUpdatedAt);
    }

}
