package io.recheck.uuidprotocol.common.firestore.model;

import com.google.cloud.firestore.Filter;

import java.util.List;
import java.util.Map;

public class WrapCompositeAndFilter extends InspectableFilter {
    public WrapCompositeAndFilter(List<WrapUnaryFilter> filters) {
        super(filters);
    }

    public WrapCompositeAndFilter(List<WrapUnaryFilter> filters, List<QueryDirection> queryDirections) {
        super(filters, queryDirections);
    }

    @Override
    public Filter toFirestoreFilter(Map<String, Object> valuesMap) {
        return Filter.and(getFirestoreFilterList(valuesMap).toArray(new Filter[0]));
    }
}
