package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperationModelArray;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class UUNodeArrayDocCreate<T_Query extends Node,
                                    T_UpdateModel extends AbstractOperationModelArray<T_Object, T_Criteria>,
                                    T_Object extends Node,
                                    T_Criteria extends Node>
        extends AbstractOperation<T_Query, T_UpdateModel> {

    private final String arrayDocFullPath;

    private String getParentPath() {
        String parentPath = "";
        if (arrayDocFullPath.lastIndexOf(".") > 0) {
            parentPath = arrayDocFullPath.substring(0,arrayDocFullPath.lastIndexOf("."));
        }
        return parentPath;
    }

    private String getRelativeDocPath() {
        return arrayDocFullPath.substring(arrayDocFullPath.lastIndexOf(".")+1);
    }

    private String getUpdatePath() {
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


    @Override
    public Query getQuery(T_Query node) {
        return new Query(Criteria.where(getParentPath() + ".uuid").is(node.getUuid()));
    }

    @Override
    public Update getUpdate(T_UpdateModel updateObject) {
        Update update = new Update();

        update.push(getUpdatePath(), MongoUtils.convertToDocument(updateObject.getUpdateObject()));

        List<Criteria> arrayCriteriaList = MongoUtils.getArrayCriteriaList(getParentPath(), "uuid", updateObject.getArrayCriteriaObject().getUuid());
        for (Criteria criteria : arrayCriteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }
}
