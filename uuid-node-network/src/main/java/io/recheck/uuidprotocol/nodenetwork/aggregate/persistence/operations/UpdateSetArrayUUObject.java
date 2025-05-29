package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UpdateSetArrayUUObject extends AbstractOperation<UUObject, UUObject, UUObject>{
    public UpdateSetArrayUUObject(String path) {
        super(path);
    }

    @Override
    protected void updateOperation(Update update, UUObject updateObject) {
        update.push(path, MongoUtils.convertToDocument(updateObject));
    }

    @Override
    public Query getQuery(UUObject queryObject) {
        return new Query(Criteria.where("uuid").is(queryObject.getUuid()));
    }
}
