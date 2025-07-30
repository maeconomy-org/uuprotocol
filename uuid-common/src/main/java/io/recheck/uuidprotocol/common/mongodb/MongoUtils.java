package io.recheck.uuidprotocol.common.mongodb;

import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MongoUtils {

    static public Document convertToDocument(Object pojo) {
        if (pojo instanceof Document) return (Document) pojo;

        Document doc = new Document();
        List<Field> allFields = ReflectionUtils.getAllFields(pojo.getClass());
        for (Field field : allFields) {

            //skip firebase id
            if (field.getName().equals("id"))
                continue;

            try {
                Object value = field.get(pojo);
                if (value == null && Iterable.class.isAssignableFrom(field.getType())) {
                    doc.put(field.getName(), new ArrayList<>());
                } else {
                    doc.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
        return doc;
    }

    /**
     * properties >> properties.$[l0]
     * properties.values >> properties.$[l0].values.$[l1]
     * properties.values.files >> properties.$[l0].values.$[l1].files.$[l2]
     * @param path
     * @return
     */
    static public String getArrayUpdatePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        String[] pathParts = path.split("\\.");
        StringBuilder updatePath = new StringBuilder();
        for (int i = 0; i < pathParts.length; i++) {
            if (i > 0) updatePath.append(".");
            updatePath.append(pathParts[i]);
            if (i < pathParts.length) {
                updatePath.append(".$[l").append(i).append("]");
            }
        }
        return updatePath.toString();
    }

    /**
     * properties >> {"l0.uuid": "2ef89665-df1d"}
     * properties.values >> {"l0.values.uuid": "2ef89665-df1d"}, {"l1.uuid": "2ef89665-df1d"}
     * properties.values.files >> {"l0.values.files.uuid": "2ef89665-df1d"}, {"l1.files.uuid": "2ef89665-df1d"}, {"l2.uuid": "2ef89665-df1d"}
     * @param path
     * @param arrayIdField
     * @param arrayIdValue
     * @return
     */
    static public List<Criteria> getArrayCriteriaList(String path, String arrayIdField, Object arrayIdValue) {
        List<Criteria> arrayFilterCriteria = new ArrayList<>();
        if (!StringUtils.hasText(path)) {
            return arrayFilterCriteria;
        }
        String[] pathParts = path.split("\\.");
        for (int i = 0; i < pathParts.length; i++) {
            String filterName = "l" + i;
            StringBuilder filterPath = new StringBuilder();
            filterPath.append(filterName);
            for (int j = i+1; j < pathParts.length; j++) {
                filterPath.append(".").append(pathParts[j]);
            }
            filterPath.append(".").append(arrayIdField);
            arrayFilterCriteria.add(Criteria.where(filterPath.toString()).is(arrayIdValue));
        }
        return arrayFilterCriteria;
    }

}
