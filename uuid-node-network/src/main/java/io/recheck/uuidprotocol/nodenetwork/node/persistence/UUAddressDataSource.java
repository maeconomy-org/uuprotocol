package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import io.recheck.uuidprotocol.domain.node.model.UUAddress;
import org.springframework.stereotype.Service;

@Service
public class UUAddressDataSource extends NodeDataSource<UUAddress>{
    public UUAddressDataSource() {
        super(UUAddress.class);
    }
}
