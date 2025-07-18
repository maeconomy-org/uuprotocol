package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUObjectNodeNetworkService extends NodeNetworkService<UUObject, UUObjectDTO> {
    public UUObjectNodeNetworkService(UUObjectDataSource uuObjectDataSource, UUIDOwnerService uuidOwnerService, AggregateService aggregateService) {
        super(uuObjectDataSource, uuidOwnerService, aggregateService);
    }
}
