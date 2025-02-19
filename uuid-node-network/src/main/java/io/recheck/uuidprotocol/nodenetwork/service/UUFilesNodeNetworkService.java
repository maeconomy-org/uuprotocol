package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUFileDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwnerService;
import org.springframework.stereotype.Service;

@Service
public class UUFilesNodeNetworkService extends NodeNetworkService<UUFile, UUFileDTO>{
    public UUFilesNodeNetworkService(UUFileDataSource uuFileDataSource, UUIDOwnerService uuidOwnerService) {
        super(uuFileDataSource, uuidOwnerService);
    }
}
