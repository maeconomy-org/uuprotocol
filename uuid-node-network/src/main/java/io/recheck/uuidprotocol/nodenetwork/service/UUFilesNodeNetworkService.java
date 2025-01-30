package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUFilesDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUFilesDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFiles;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import org.springframework.stereotype.Service;

@Service
public class UUFilesNodeNetworkService extends NodeNetworkService<UUFiles, UUFilesDTO>{
    public UUFilesNodeNetworkService(UUFilesDataSource uuFilesDataSource, OwnerUUIDService ownerUUIDService) {
        super(uuFilesDataSource, ownerUUIDService);
    }
}
