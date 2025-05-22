package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UpdatePushUUObjectToArray extends AbstractOperation<UUObject, UUObject, UUObject> {
    public UpdatePushUUObjectToArray(String path) {
        super(path);
    }

    @Override
    protected void updateOperation(Update update, UUObject updateObject) {
        update.push(path, updateObject.getUuid());
    }

    @Override
    public Query getQuery(UUObject queryObject) {
        return new Query(Criteria.where("uuid").is(queryObject.getUuid()));
    }
}
