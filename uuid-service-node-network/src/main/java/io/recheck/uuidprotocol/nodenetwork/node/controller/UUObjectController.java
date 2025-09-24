package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUObjectNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUObject")
public class UUObjectController extends NodeController<UUObject, UUObjectDTO> {
    public UUObjectController(UUObjectNodeNetworkService uuObjectNodeNetworkService, UUObjectDataSource uuObjectDataSource) {
        super(uuObjectNodeNetworkService, uuObjectDataSource);
    }
}
