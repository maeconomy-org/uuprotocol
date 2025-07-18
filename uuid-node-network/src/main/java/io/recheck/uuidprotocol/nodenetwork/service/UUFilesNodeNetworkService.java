package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUFilesNodeNetworkService extends NodeNetworkService<UUFile, UUFileDTO>{
    public UUFilesNodeNetworkService(UUFileDataSource uuFileDataSource, UUIDOwnerService uuidOwnerService, AggregateService aggregateService) {
        super(uuFileDataSource, uuidOwnerService, aggregateService);
    }
}
