package io.recheck.uuidprotocol.common.firestore.model;

import com.google.cloud.firestore.Query;
import lombok.Data;

@Data
public class QueryDirection {
    private final String field;
    private Query.Direction direction;

    public QueryDirection(String field) {
        this.field = field;
    }

    public QueryDirection(String field, Query.Direction direction) {
        this(field);
        this.direction = direction;
    }

}
