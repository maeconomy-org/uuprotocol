package io.recheck.uuidprotocol.common.querybuilder.model;

import com.google.firestore.v1.StructuredQuery;
import lombok.Data;

import java.util.List;

@Data
public class QueryUnaryFilter {

    private String field;
    private StructuredQuery.FieldFilter.Operator operator;
    private List<Object> value;

}
