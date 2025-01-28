package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyNodeNetworkService extends NodeNetworkService<UUProperty, UUPropertyDTO> {
    public UUPropertyNodeNetworkService(UUPropertyDataSource uuPropertyDataSource, OwnerUUIDService ownerUUIDService) {
        super(uuPropertyDataSource, ownerUUIDService);
    }
}
