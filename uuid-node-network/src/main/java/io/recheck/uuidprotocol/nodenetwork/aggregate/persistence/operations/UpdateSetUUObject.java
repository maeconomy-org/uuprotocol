package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

public class UpdateSetUUObject extends AbstractOperation<UUObject, UUObject, UUObject> {
    public UpdateSetUUObject() {
        super("");
    }

    @Override
    protected void updateOperation(Update update, UUObject updateObject) {
        Document updateObjectDoc = MongoUtils.convertToDocument(updateObject);
        for (Map.Entry<String, Object> arrayDocEntry : updateObjectDoc.entrySet()) {
            update.set(arrayDocEntry.getKey(), arrayDocEntry.getValue());
        }
    }

    @Override
    public Query getQuery(UUObject queryObject) {
        return new Query(Criteria.where("uuid").is(queryObject.getUuid()));
    }
}
