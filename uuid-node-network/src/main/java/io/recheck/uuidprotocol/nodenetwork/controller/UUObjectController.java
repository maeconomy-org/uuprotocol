package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.domain.node.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUObjectDTO;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.service.UUObjectNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUObject")
public class UUObjectController extends NodeNetworkController<UUObject, UUObjectDTO>{
    public UUObjectController(UUObjectNodeNetworkService uuObjectNodeNetworkService, UUObjectDataSource uuObjectDataSource) {
        super(uuObjectNodeNetworkService, uuObjectDataSource);
    }
}
