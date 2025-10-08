package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FirestoreUtils {

    @SneakyThrows
    public static List<Filter> toFirestoreFilters(Object filterCriteria) {
        List<Filter> filters = new ArrayList<>();

        List<Field> allFields = ReflectionUtils.getNonNullFields(filterCriteria);
        for (Field field : allFields) {
            Object value = field.get(filterCriteria);
            filters.add(Filter.equalTo(field.getName(), value));
        }

        return filters;
    }

}
