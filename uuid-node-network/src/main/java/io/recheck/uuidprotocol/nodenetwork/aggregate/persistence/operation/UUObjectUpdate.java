package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

public class UUObjectUpdate extends AbstractOperation<UUObject, UUObject> {
    @Override
    public Query getQuery(UUObject uuObject) {
        return new Query(Criteria.where("uuid").is(uuObject.getUuid()));
    }

    @Override
    public Update getUpdate(UUObject updateObject) {
        Update update = new Update();
        Document updateObjectDoc = MongoUtils.convertToDocument(updateObject);
        for (Map.Entry<String, Object> updateObjectDocEntry : updateObjectDoc.entrySet()) {
            update.set(updateObjectDocEntry.getKey(), updateObjectDocEntry.getValue());
        }
        return update;
    }
}
