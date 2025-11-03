package io.recheck.uuidprotocol.common.firestore.model;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public abstract class InspectableFilter {

    private final List<WrapUnaryFilter> filters;
    private final Map<String, WrapUnaryFilter> filtersMap;

    private List<QueryDirection> queryDirections;
    private Map<String, Query.Direction> queryDirectionsMap;

    public InspectableFilter(List<WrapUnaryFilter> filters) {
        this.filters = filters;

        filtersMap = filters.stream()
                        .collect(Collectors.toMap(wrapUnaryFilter -> wrapUnaryFilter.getField(), Function.identity()));
    }

    public InspectableFilter(List<WrapUnaryFilter> filters, List<QueryDirection> queryDirections) {
        this(filters);
        this.queryDirections = queryDirections;

        queryDirectionsMap = queryDirections.stream()
                .collect(Collectors.toMap(queryDirection -> queryDirection.getField(), QueryDirection::getDirection));
    }

    protected List<Filter> getFirestoreFilterList(Map<String, Object> valuesMap) {
        List<Filter> filters = new ArrayList<>();
        for (Map.Entry<String, Object> valueEntry : valuesMap.entrySet()) {
            filters.add(filtersMap.get(valueEntry.getKey()).toFirestoreFilter(valueEntry.getValue()));
        }
        return filters;
    }

    public abstract Filter toFirestoreFilter(Map<String, Object> valuesMap);



}
