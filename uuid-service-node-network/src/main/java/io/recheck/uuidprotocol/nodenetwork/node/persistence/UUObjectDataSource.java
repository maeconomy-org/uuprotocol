package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.model.WrapCompositeAndFilter;
import io.recheck.uuidprotocol.common.firestore.model.InspectableFilter;
import io.recheck.uuidprotocol.common.firestore.model.QueryDirection;
import io.recheck.uuidprotocol.common.firestore.model.WrapUnaryEqualToFilter;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UUObjectDataSource extends NodeDataSource<UUObject> {

    WrapUnaryEqualToFilter uuidFilter = new WrapUnaryEqualToFilter("uuid");
    WrapUnaryEqualToFilter softDeletedFilter = new WrapUnaryEqualToFilter("softDeleted");
    QueryDirection softDeletedAtAsc = new QueryDirection("softDeletedAt", Query.Direction.ASCENDING);

    InspectableFilter findDeleted = new WrapCompositeAndFilter(List.of(uuidFilter, softDeletedFilter), List.of(softDeletedAtAsc));


    public UUObjectDataSource() {
        super(UUObject.class);
    }

    public List<UUObject> findDeleted(String uuid) {
        return where(findDeleted.toFirestoreFilter(Map.of("uuid", uuid, "softDeleted", true)),
                findDeleted.getQueryDirectionsMap());
    }
}