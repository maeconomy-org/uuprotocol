package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class HistoryDelete {

    public Query getQuery(UUObject uuObject) {
        return new Query(Criteria.where("uuid").is(uuObject.getUuid()));
    }

    public Update getUpdate() {
        return new Update().unset("history");
    }
}
