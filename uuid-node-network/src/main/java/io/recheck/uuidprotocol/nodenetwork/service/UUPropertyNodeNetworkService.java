package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyNodeNetworkService extends NodeNetworkService<UUProperty, UUPropertyDTO> {
    public UUPropertyNodeNetworkService(UUPropertyDataSource uuPropertyDataSource, UUIDOwnerService uuidOwnerService, AggregateService aggregateService) {
        super(uuPropertyDataSource, uuidOwnerService, aggregateService);
    }
}
