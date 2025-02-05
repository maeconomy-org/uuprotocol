package io.recheck.uuidprotocol.domain.node.datasource;

import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyValueDataSource extends NodeDataSource<UUPropertyValue> {
    public UUPropertyValueDataSource() {
        super(UUPropertyValue.class);
    }
}
