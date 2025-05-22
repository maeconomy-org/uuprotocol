package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.data.mongodb.core.query.Update;

public class UpdatePullUUObjectFromArray extends UpdatePushUUObjectToArray{
    public UpdatePullUUObjectFromArray(String path) {
        super(path);
    }
    @Override
    protected void updateOperation(Update update, UUObject updateObject) {
        update.pull(path, updateObject.getUuid());
    }
}
