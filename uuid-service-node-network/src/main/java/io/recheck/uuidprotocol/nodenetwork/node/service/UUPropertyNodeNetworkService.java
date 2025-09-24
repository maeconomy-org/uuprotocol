package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUPropertyEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.registrar.UUIDRegistrarService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyNodeNetworkService extends NodeNetworkService<UUProperty, UUPropertyDTO> {
    public UUPropertyNodeNetworkService(UUPropertyDataSource uuPropertyDataSource, AggregateUUPropertyEventListener aggregateUUPropertyEventListener, UUIDRegistrarService uuidRegistrarService) {
        super(uuPropertyDataSource, aggregateUUPropertyEventListener, uuidRegistrarService);
    }
}
