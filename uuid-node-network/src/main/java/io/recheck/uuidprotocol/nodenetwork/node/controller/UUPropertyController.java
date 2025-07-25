package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUPropertyNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUProperty")
public class UUPropertyController extends NodeController<UUProperty, UUPropertyDTO> {
    public UUPropertyController(UUPropertyNodeNetworkService uuPropertyNodeNetworkService, UUPropertyDataSource uuPropertyDataSource) {
        super(uuPropertyNodeNetworkService, uuPropertyDataSource);
    }
}
