package io.recheck.uuidprotocol.nodenetwork.datasource;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.domain.node.model.Node;

import java.util.List;
import java.util.Map;

public class NodeDataSource<T extends Node> extends AuditDataSource<T> {

    public NodeDataSource(Class<T> type) {
        super(type);
    }

    public T findByUUID(String uuid) {
        Filter filter = Filter.equalTo("uuid", uuid);
        return whereFindFirst(filter);
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

    public List<T> findDeleted(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", true));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("softDeletedAt", Query.Direction.ASCENDING);
        return where(filter, orderByLastUpdatedAt);
    }

}
