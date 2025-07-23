package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUPropertyValueEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyValueNodeNetworkService extends NodeNetworkService<UUPropertyValue, UUPropertyValueDTO> {
    public UUPropertyValueNodeNetworkService(UUPropertyValueDataSource uuPropertyValueDataSource, AggregateUUPropertyValueEventListener aggregateUUPropertyValueEventListener, UUIDOwnerService uuidOwnerService) {
        super(uuPropertyValueDataSource, aggregateUUPropertyValueEventListener, uuidOwnerService);
    }
}
