package io.recheck.uuidprotocol.common.mongodb.query;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class UpdateArrayDoc {

    private final String arrayDocFullPath;
    private final String arrayIdField;
    private final String parentIdField;

    public String getParentPath() {
        String parentPath = "";
        if (arrayDocFullPath.lastIndexOf(".") > 0) {
            parentPath = arrayDocFullPath.substring(0,arrayDocFullPath.lastIndexOf("."));
        }
        return parentPath;
    }

    private String getRelativeDocPath() {
        return arrayDocFullPath.substring(arrayDocFullPath.lastIndexOf(".")+1);
    }

    private String getUpdatePushPath() {
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

    private String getUpdateSetPath() {
        return MongoUtils.getArrayUpdatePath(arrayDocFullPath);
    }


    public Update pushArrayDoc(Object parentIdValue, Object arrayObject) {
        Update update = new Update();

        update.push(getUpdatePushPath(), MongoUtils.convertToDocument(arrayObject));

        List<Criteria> criteriaList = MongoUtils.getArrayCriteriaList(getParentPath(), parentIdField, parentIdValue);
        for (Criteria criteria : criteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }

    public Update setArrayDoc(Object arrayIdValue, Object arrayObject) {
        Update update = new Update();

        Document updateObjectDoc = MongoUtils.convertToDocument(arrayObject);
        String updatePath = getUpdateSetPath();
        for (Map.Entry<String, Object> arrayDocEntry : updateObjectDoc.entrySet()) {
            update.set(updatePath + "." + arrayDocEntry.getKey(), arrayDocEntry.getValue());
        }

        List<Criteria> criteriaList = MongoUtils.getArrayCriteriaList(arrayDocFullPath, arrayIdField, arrayIdValue);
        for (Criteria criteria : criteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }

    public Update pullArrayDoc(Object parentIdValue, Object arrayIdValue) {
        Update update = new Update();

        update.pull(getUpdatePushPath(), new Document(arrayIdField, arrayIdValue));

        List<Criteria> criteriaList = MongoUtils.getArrayCriteriaList(getParentPath(), parentIdField, parentIdValue);
        for (Criteria criteria : criteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }

}
