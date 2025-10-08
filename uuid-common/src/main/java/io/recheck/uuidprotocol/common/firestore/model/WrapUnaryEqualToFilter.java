package io.recheck.uuidprotocol.common.firestore.model;

import com.google.cloud.firestore.Filter;

public class WrapUnaryEqualToFilter extends WrapUnaryFilter {
    public WrapUnaryEqualToFilter(String field) {
        super(field);
    }

    @Override
    public Filter toFirestoreFilter(Object value) {
        return Filter.equalTo(this.getField(), value);
    }
}
