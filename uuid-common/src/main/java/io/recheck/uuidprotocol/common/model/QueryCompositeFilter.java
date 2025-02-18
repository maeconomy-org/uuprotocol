package io.recheck.uuidprotocol.common.model;

import com.google.firestore.v1.StructuredQuery;
import lombok.Data;

import java.util.List;

@Data
public class QueryCompositeFilter {

    private StructuredQuery.CompositeFilter.Operator compositeOperator;
    private List<QueryUnaryFilter> queryUnaryFilters;

    private List<QueryCompositeFilter> queryCompositeFilters;

}
