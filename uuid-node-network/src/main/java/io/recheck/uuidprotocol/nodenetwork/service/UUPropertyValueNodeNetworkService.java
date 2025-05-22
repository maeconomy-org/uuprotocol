package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUPropertyValueNodeNetworkService extends NodeNetworkService<UUPropertyValue, UUPropertyValueDTO> {
    public UUPropertyValueNodeNetworkService(UUPropertyValueDataSource uuPropertyValueDataSource, UUIDOwnerService uuidOwnerService, AggregateService aggregateService) {
        super(uuPropertyValueDataSource, uuidOwnerService, aggregateService);
    }
}
