package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyDataSource extends NodeDataSource<UUProperty> {
    public UUPropertyDataSource() {
        super(UUProperty.class);
    }
}
