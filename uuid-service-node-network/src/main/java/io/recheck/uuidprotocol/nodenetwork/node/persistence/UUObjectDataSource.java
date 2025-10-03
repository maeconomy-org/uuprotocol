package io.recheck.uuidprotocol.nodenetwork.node.persistence;
import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UUObjectDataSource extends NodeDataSource<UUObject> {

    public UUObjectDataSource() {
        super(UUObject.class);
    }

    public List<UUObject> findDeleted(String uuid) {
        Filter filter = Filter.and(Filter.equalTo("uuid", uuid), Filter.equalTo("softDeleted", true));
        Map<String, Query.Direction> orderByLastUpdatedAt = Map.of("softDeletedAt", Query.Direction.ASCENDING);
        return where(filter, orderByLastUpdatedAt);
    }

}
