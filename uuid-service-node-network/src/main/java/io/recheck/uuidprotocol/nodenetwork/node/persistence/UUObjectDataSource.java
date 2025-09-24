package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import org.springframework.stereotype.Service;

@Service
public class UUObjectDataSource extends NodeDataSource<UUObject> {
    public UUObjectDataSource() {
        super(UUObject.class);
    }
}
