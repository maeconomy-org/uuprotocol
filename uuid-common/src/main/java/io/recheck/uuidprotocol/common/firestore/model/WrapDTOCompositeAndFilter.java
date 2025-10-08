package io.recheck.uuidprotocol.common.firestore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WrapDTOCompositeAndFilter<T> {

    @Getter
    private Map<String, WrapCompositeAndFilter> inspectableFilterMap;
    private ObjectMapper objectMapper;

    public WrapDTOCompositeAndFilter(Class<T> type, List<WrapUnaryFilter> additionalFilters) {
        this(type, additionalFilters, null);
    }

    public WrapDTOCompositeAndFilter(Class<T> type, List<WrapUnaryFilter> additionalWrapUnaryFilters, List<QueryDirection> queryDirections) {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        inspectableFilterMap = new HashMap<>();

        List<List<String>> fieldCombinationsList = ReflectionUtils.getFieldCombinations(type);
        for (List<String> fieldCombinations : fieldCombinationsList) {
            List<WrapUnaryFilter> wrapUnaryFilters = new ArrayList<>();

            for (String field : fieldCombinations) {
                wrapUnaryFilters.add(new WrapUnaryEqualToFilter(field));
            }
            String key = wrapUnaryFilters.stream().map(w -> w.getField()).collect(Collectors.joining(", "));

            if (additionalWrapUnaryFilters != null && !additionalWrapUnaryFilters.isEmpty()) {
                wrapUnaryFilters.addAll(additionalWrapUnaryFilters);
            }

            WrapCompositeAndFilter wrapFilter;
            if (queryDirections != null && !queryDirections.isEmpty()) {
                wrapFilter = new WrapCompositeAndFilter(wrapUnaryFilters, queryDirections);
            } else {
                wrapFilter = new WrapCompositeAndFilter(wrapUnaryFilters);
            }
            inspectableFilterMap.put(key, wrapFilter);
        }
    }

    public WrapCompositeAndFilter getWrapFilter(T dtoObject) {
        List<String> fields = ReflectionUtils.getNonNullFields(dtoObject).stream().map(Field::getName).toList();
        String key = String.join(", ", fields);
        return inspectableFilterMap.get(key);
    }

    public Filter toFirestoreFilter(WrapCompositeAndFilter wrapCompositeAndFilter, T dtoObject, Map<String, Object> additionalValuesMap) {
        Map<String, Object> dtoObjectValueMap = objectMapper.convertValue(dtoObject, new TypeReference<>() {});
        dtoObjectValueMap.putAll(additionalValuesMap);
        return wrapCompositeAndFilter.toFirestoreFilter(dtoObjectValueMap);
    }

}
