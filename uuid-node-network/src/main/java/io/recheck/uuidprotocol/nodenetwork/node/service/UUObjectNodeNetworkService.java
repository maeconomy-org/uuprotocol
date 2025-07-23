package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUObjectEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUObjectNodeNetworkService extends NodeNetworkService<UUObject, UUObjectDTO> {
    public UUObjectNodeNetworkService(UUObjectDataSource uuObjectDataSource, AggregateUUObjectEventListener aggregateUUObjectEventListener, UUIDOwnerService uuidOwnerService) {
        super(uuObjectDataSource, aggregateUUObjectEventListener, uuidOwnerService);
    }
}
