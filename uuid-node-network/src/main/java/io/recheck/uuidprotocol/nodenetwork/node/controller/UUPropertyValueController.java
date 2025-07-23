package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUPropertyValueNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUPropertyValue")
public class UUPropertyValueController extends NodeController<UUPropertyValue, UUPropertyValueDTO> {
    public UUPropertyValueController(UUPropertyValueNodeNetworkService uuPropertyNodeNetworkService, UUPropertyValueDataSource uuPropertyValueDataSource) {
        super(uuPropertyNodeNetworkService, uuPropertyValueDataSource);
    }
}
