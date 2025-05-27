package io.recheck.uuidprotocol.common.firestore.querybuilder.model;

import com.google.cloud.firestore.Query;
import com.google.firestore.v1.StructuredQuery;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryCompositeFilter {

    private StructuredQuery.CompositeFilter.Operator compositeOperator;
    private List<QueryUnaryFilter> queryUnaryFilters;

    private List<QueryCompositeFilter> queryCompositeFilters;

    private Map<String, Query.Direction> orderByFields;

}
