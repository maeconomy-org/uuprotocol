package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class HistoryCreate extends AbstractOperation<UUObject, UUObject> {

    @Override
    public Query getQuery(UUObject uuObject) {
        return new Query(Criteria.where("uuid").is(uuObject.getUuid()));
    }

    @Override
    public Update getUpdate(UUObject updateObject) {
        Update update = new Update();
        update.push("history", MongoUtils.convertToDocument(updateObject));
        return update;
    }

}
