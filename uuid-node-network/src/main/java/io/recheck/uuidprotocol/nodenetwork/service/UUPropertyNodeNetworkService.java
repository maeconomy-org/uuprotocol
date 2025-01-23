package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyNodeNetworkService extends NodeNetworkService<UUProperty, UUPropertyDTO> {
    public UUPropertyNodeNetworkService(UUPropertyDataSource uuPropertyDataSource, ClientUUIDService clientUUIDService) {
        super(uuPropertyDataSource, clientUUIDService);
    }
}
