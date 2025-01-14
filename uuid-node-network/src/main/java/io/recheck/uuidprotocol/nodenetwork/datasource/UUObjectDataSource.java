package io.recheck.uuidprotocol.nodenetwork.datasource;

import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import org.springframework.stereotype.Service;

@Service
public class UUObjectDataSource extends AuditDataSource<UUObject> {
    public UUObjectDataSource() {
        super(UUObject.class);
    }
}
