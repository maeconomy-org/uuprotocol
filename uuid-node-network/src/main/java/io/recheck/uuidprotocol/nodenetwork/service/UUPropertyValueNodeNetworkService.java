package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyValueNodeNetworkService extends NodeNetworkService<UUPropertyValue, UUPropertyValueDTO> {
    public UUPropertyValueNodeNetworkService(UUPropertyValueDataSource uuPropertyValueDataSource, OwnerUUIDService ownerUUIDService) {
        super(uuPropertyValueDataSource, ownerUUIDService);
    }
}
