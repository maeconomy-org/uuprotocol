package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.domain.node.dto.UUAddressDTO;
import io.recheck.uuidprotocol.domain.node.model.UUAddress;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUAddressEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUAddressDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUAddressNodeNetworkService extends NodeNetworkService<UUAddress, UUAddressDTO> {
    public UUAddressNodeNetworkService(UUAddressDataSource uuAddressDataSource, AggregateUUAddressEventListener aggregateUUAddressEventListener, UUIDOwnerService uuidOwnerService) {
        super(uuAddressDataSource, aggregateUUAddressEventListener, uuidOwnerService);
    }
}
