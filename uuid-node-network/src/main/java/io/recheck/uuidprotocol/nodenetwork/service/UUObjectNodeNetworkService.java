package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUObjectDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import org.springframework.stereotype.Service;

@Service
public class UUObjectNodeNetworkService extends NodeNetworkService<UUObject, UUObjectDTO> {
    public UUObjectNodeNetworkService(UUObjectDataSource uuObjectDataSource, ClientUUIDService clientUUIDService) {
        super(uuObjectDataSource, clientUUIDService);
    }
}
