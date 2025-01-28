package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import org.springframework.stereotype.Service;

@Service
public class UUObjectNodeNetworkService extends NodeNetworkService<UUObject, UUObjectDTO> {
    public UUObjectNodeNetworkService(UUObjectDataSource uuObjectDataSource, OwnerUUIDService ownerUUIDService) {
        super(uuObjectDataSource, ownerUUIDService);
    }
}
