package io.recheck.uuidprotocol.nodenetwork.datasource;

import io.recheck.uuidprotocol.nodenetwork.model.UUPropertyValue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UUPropertyValueDataSource extends AuditDataSource<UUPropertyValue> {
    public UUPropertyValueDataSource() {
        super(UUPropertyValue.class);
    }

    public List<UUPropertyValue> findByPropertyId(String propertyId) {
        return whereEqualTo("propertyId", propertyId);
    }
}
