package io.recheck.uuidprotocol.nodenetwork.datasource;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.domain.node.model.Node;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NodeDataSource<T extends Node> extends AuditDataSource<T> {

    public NodeDataSource(Class<T> type) {
        super(type);
    }

    public T findByUUID(String uuid) {
        Filter filter = Filter.equalTo("uuid", uuid);
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        Optional<T> firstNodeOptional = where(filter, orderByLastUpdatedAt).stream().findFirst();
        return firstNodeOptional.orElse(null);
    }

    public T findByUUIDAndSoftDeletedFalse(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", false));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);
        Optional<T> firstNodeOptional = where(filter, orderByLastUpdatedAt).stream().findFirst();
        return firstNodeOptional.orElse(null);
    }

    public List<T> findByOrFindAll(String uuid, String createdBy, Boolean softDeleted) {
        List<Filter> filters = new ArrayList<>();
        if (StringUtils.hasText(createdBy)) {
            filters.add(Filter.equalTo("createdBy", createdBy));
        }
        if (softDeleted != null) {
            filters.add(Filter.equalTo("softDeleted", softDeleted));
        }
        if (StringUtils.hasText(uuid)) {
            filters.add(Filter.equalTo("uuid", uuid));
        }

        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("lastUpdatedAt", Query.Direction.DESCENDING);

        return where(Filter.and(filters.toArray(new Filter[0])), orderByLastUpdatedAt);
    }

}
