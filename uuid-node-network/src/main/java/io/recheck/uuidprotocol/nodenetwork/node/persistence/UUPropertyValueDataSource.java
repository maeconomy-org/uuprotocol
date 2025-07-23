package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyValueDataSource extends NodeDataSource<UUPropertyValue> {
    public UUPropertyValueDataSource() {
        super(UUPropertyValue.class);
    }
}
