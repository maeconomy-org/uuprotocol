package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.domain.node.datasource.UUFilesDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUFilesDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFiles;
import io.recheck.uuidprotocol.nodenetwork.service.UUFilesNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUFile")
public class UUFilesController extends NodeNetworkController<UUFiles, UUFilesDTO> {
    public UUFilesController(UUFilesNodeNetworkService uuFilesNodeNetworkService, UUFilesDataSource uuFilesDataSource) {
        super(uuFilesNodeNetworkService, uuFilesDataSource);
    }
}
