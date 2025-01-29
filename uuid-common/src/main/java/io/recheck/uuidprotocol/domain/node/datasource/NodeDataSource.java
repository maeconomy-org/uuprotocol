package io.recheck.uuidprotocol.domain.node.datasource;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.domain.node.model.Node;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

public class NodeDataSource<T extends Node> extends AuditDataSource<T> {

    public NodeDataSource(Class<T> type) {
        super(type);
    }

    public T findByUUIDAndSoftDeletedFalse(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", false));
        Optional<T> firstNodeOptional = where(filter).stream().findFirst();
        return firstNodeOptional.orElse(null);
    }

    public List<T> findByOrFindAll(String uuid, String createdBy, Boolean softDeleted) {
        return whereAndFiltersOrFindAll(filtersBy(uuid, createdBy, softDeleted));
    }

    private List<Filter> filtersBy(String uuid, String createdBy, Boolean softDeleted) {
        List<Filter> filters = filtersBy(createdBy, softDeleted);
        if (StringUtils.hasText(uuid)) {
            filters.add(Filter.equalTo("uuid", uuid));
        }
        return filters;
    }

}
