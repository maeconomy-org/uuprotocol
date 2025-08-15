package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class UUNodeArrayUUIDAdd<T_Query extends Node, T_Object extends Node> extends AbstractOperation<T_Query, T_Object> {

    private final String arrayDocFullPath;

    private String getParentPath() {
        String parentPath = "";
        if (arrayDocFullPath.lastIndexOf(".") > 0) {
            parentPath = arrayDocFullPath.substring(0,arrayDocFullPath.lastIndexOf("."));
        }
        return parentPath;
    }

    @Override
    public Query getQuery(T_Query node) {
        return new Query(Criteria.where(getParentPath() + ".uuid").is(node.getUuid()));
    }

    @Override
    public Update getUpdate(T_Object updateObject) {
        Update update = new Update();
        update.push(arrayDocFullPath, updateObject.getUuid());
        return update;
    }
}
