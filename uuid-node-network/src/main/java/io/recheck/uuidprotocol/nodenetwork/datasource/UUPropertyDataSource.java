package io.recheck.uuidprotocol.nodenetwork.datasource;

import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyDataSource extends AuditDataSource<UUProperty> {
    public UUPropertyDataSource() {
        super(UUProperty.class);
    }
}
