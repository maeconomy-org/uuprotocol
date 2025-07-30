package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import lombok.Data;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

@Data
public class UUNodeArrayDocUpdate<T_Object extends Node> extends AbstractOperation<T_Object, T_Object> {

    private final String arrayDocFullPath;

    @Override
    public Query getQuery(T_Object node) {
        return new Query(Criteria.where(arrayDocFullPath + ".uuid").is(node.getUuid()));
    }

    @Override
    public Update getUpdate(T_Object updateObject) {
        Update update = new Update();

        String updatePath = MongoUtils.getArrayUpdatePath(arrayDocFullPath);
        Document updateObjectDoc = MongoUtils.convertToDocument(updateObject);
        for (Map.Entry<String, Object> updateObjectDocEntry : updateObjectDoc.entrySet()) {
            update.set(updatePath + "." + updateObjectDocEntry.getKey(), updateObjectDocEntry.getValue());
        }

        List<Criteria> arrayCriteriaList = MongoUtils.getArrayCriteriaList(arrayDocFullPath, "uuid", updateObject.getUuid());
        for (Criteria criteria : arrayCriteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }
}
