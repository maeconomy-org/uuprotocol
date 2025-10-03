package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FirestoreUtils {

    @SneakyThrows
    public static String getId(Object object) {
        return ReflectionUtils.getValueAnnotationPresent(FirestoreId.class, object);
    }

    @SneakyThrows
    public static void setId(Object object, String id) {
        ReflectionUtils.setValueAnnotationPresent(FirestoreId.class, object, id);
    }

    @SneakyThrows
    public static List<Filter> getFilters(Object filterCriteria) {
        List<Filter> filters = new ArrayList<>();

        List<Field> allFields = ReflectionUtils.getAllFields(filterCriteria.getClass());
        for (Field field : allFields) {
            Object value = field.get(filterCriteria);
            if (value != null) { //filter by all fields that has non empty value
                if (value instanceof String) {
                    if (!StringUtils.hasText((CharSequence) value)) {
                        continue;
                    }
                }
                filters.add(Filter.equalTo(field.getName(), value));
            }
        }

        return filters;
    }

}
