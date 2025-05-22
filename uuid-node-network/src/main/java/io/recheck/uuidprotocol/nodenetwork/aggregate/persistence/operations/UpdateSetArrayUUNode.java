package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

public class UpdateSetArrayUUNode<T extends Node> extends AbstractOperation<T,T,T> {
    public UpdateSetArrayUUNode(String path) {
        super(path);
    }

    @Override
    protected String getUpdatePath() {
        return MongoUtils.getArrayUpdatePath(path);
    }

    @Override
    public List<Criteria> getArrayCriteriaList(T arrayCriteriaObject) {
        return MongoUtils.getArrayCriteriaList(path, "uuid", arrayCriteriaObject.getUuid());
    }

    @Override
    protected void updateOperation(Update update, T updateObject) {
        Document updateObjectDoc = MongoUtils.convertToDocument(updateObject);
        String updatePath = getUpdatePath();
        for (Map.Entry<String, Object> arrayDocEntry : updateObjectDoc.entrySet()) {
            update.set(updatePath + "." + arrayDocEntry.getKey(), arrayDocEntry.getValue());
        }
    }

    @Override
    public Query getQuery(T queryObject) {
        return new Query(Criteria.where(path+".uuid").is(queryObject.getUuid()));
    }
}
