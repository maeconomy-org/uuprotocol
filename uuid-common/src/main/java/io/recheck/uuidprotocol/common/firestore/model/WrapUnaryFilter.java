package io.recheck.uuidprotocol.common.firestore.model;

import com.google.cloud.firestore.Filter;
import lombok.Data;

@Data
abstract public class WrapUnaryFilter {
    private final String field;
    private Object value;

    public WrapUnaryFilter(String field) {
        this.field = field;
    }

    public abstract Filter toFirestoreFilter(Object value);
}
