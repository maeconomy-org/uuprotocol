package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.domain.node.datasource.UUFileDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.service.UUFilesNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUFile")
public class UUFilesController extends NodeNetworkController<UUFile, UUFileDTO> {
    public UUFilesController(UUFilesNodeNetworkService uuFilesNodeNetworkService, UUFileDataSource uuFileDataSource) {
        super(uuFilesNodeNetworkService, uuFileDataSource);
    }
}
