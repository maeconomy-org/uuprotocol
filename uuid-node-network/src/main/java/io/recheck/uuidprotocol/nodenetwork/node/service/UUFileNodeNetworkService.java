package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUFileEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUFileNodeNetworkService extends NodeNetworkService<UUFile, UUFileDTO>{
    public UUFileNodeNetworkService(UUFileDataSource uuFileDataSource, AggregateUUFileEventListener aggregateUUFileEventListener, UUIDOwnerService uuidOwnerService) {
        super(uuFileDataSource, aggregateUUFileEventListener, uuidOwnerService);
    }
}
