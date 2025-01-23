package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUObjectDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.service.NodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUObject")
public class UUObjectController extends NodeNetworkController<UUObject, UUObjectDTO>{
    public UUObjectController(NodeNetworkService<UUObject, UUObjectDTO> uuObjectNodeNetworkService, UUObjectDataSource uuObjectDataSource) {
        super(uuObjectNodeNetworkService, uuObjectDataSource);
    }
}
