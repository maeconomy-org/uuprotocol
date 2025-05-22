package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;

public class UpdatePushUUNodeToArray<T extends Node, V extends Node> extends AbstractOperation<T,V,V>{
    public UpdatePushUUNodeToArray(String path) {
        super(path);
    }

    protected String getUpdatePath() {
        String updatePath;
        String parentPath = getParentPath();
        if (StringUtils.hasText(parentPath)) {
            updatePath = MongoUtils.getArrayUpdatePath(parentPath) + "." + getRelativeDocPath();
        }
        else {
            updatePath = getRelativeDocPath();
        }
        return updatePath;
    }

    public List<Criteria> getArrayCriteriaList(V arrayCriteriaObject) {
        return MongoUtils.getArrayCriteriaList(getParentPath(), "uuid", arrayCriteriaObject.getUuid());
    }

    @Override
    protected void updateOperation(Update update, T updateObject) {
        update.push(getUpdatePath(), MongoUtils.convertToDocument(updateObject));
    }

    @Override
    public Query getQuery(V queryObject) {
        return new Query(Criteria.where(getParentPath() + ".uuid").is(queryObject.getUuid()));
    }
}
