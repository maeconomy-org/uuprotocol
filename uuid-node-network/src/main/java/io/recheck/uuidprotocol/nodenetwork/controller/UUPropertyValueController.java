package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.domain.node.datasource.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.service.UUPropertyValueNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUPropertyValue")
public class UUPropertyValueController extends NodeNetworkController<UUPropertyValue, UUPropertyValueDTO> {
    public UUPropertyValueController(UUPropertyValueNodeNetworkService uuPropertyNodeNetworkService, UUPropertyValueDataSource uuPropertyValueDataSource) {
        super(uuPropertyNodeNetworkService, uuPropertyValueDataSource);
    }
}
