package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractOperation<T,V,Z> {

    protected final String path;

    protected String getParentPath() {
        String parentPath = "";
        if (path.lastIndexOf(".") > 0) {
            parentPath = path.substring(0,path.lastIndexOf("."));
        }
        return parentPath;
    }

    protected String getRelativeDocPath() {
        return path.substring(path.lastIndexOf(".")+1);
    }

    protected String getUpdatePath() {
        return "";
    }

    public List<Criteria> getArrayCriteriaList(V arrayCriteriaObject) {
        return new ArrayList<>();
    }

    public Update getUpdate(T updateObject) {
        return getUpdate(updateObject, (V) updateObject);
    }

    public Update getUpdate(T updateObject, V arrayCriteriaObject)  {
        Update update = new Update();

        updateOperation(update, updateObject);

        List<Criteria> criteriaList = getArrayCriteriaList(arrayCriteriaObject);
        for (Criteria criteria : criteriaList) {
            update.filterArray(criteria);
        }

        return update;
    }

    protected abstract void updateOperation(Update update, T updateObject);

    public abstract Query getQuery(Z queryObject);

}
