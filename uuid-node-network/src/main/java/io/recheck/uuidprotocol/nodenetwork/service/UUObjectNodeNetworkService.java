package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUObjectNodeNetworkService extends NodeNetworkService<UUObject, UUObjectDTO> {
    public UUObjectNodeNetworkService(UUObjectDataSource uuObjectDataSource, UUIDOwnerService uuidOwnerService) {
        super(uuObjectDataSource, uuidOwnerService);
    }
}
