package io.recheck.uuidprotocol.domain.node.datasource;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.stereotype.Service;

@Service
public class UUObjectDataSource extends AuditDataSource<UUObject> {
    public UUObjectDataSource() {
        super(UUObject.class);
    }
}
