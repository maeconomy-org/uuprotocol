package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUFileNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUFile")
public class UUFileController extends NodeController<UUFile, UUFileDTO> {
    public UUFileController(UUFileNodeNetworkService uuFileNodeNetworkService, UUFileDataSource uuFileDataSource) {
        super(uuFileNodeNetworkService, uuFileDataSource);
    }
}
