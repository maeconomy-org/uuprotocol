package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUAddressDTO;
import io.recheck.uuidprotocol.domain.node.model.UUAddress;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUAddressDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUAddressNodeNetworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUAddress")
public class UUAddressController extends NodeController<UUAddress, UUAddressDTO>{
    public UUAddressController(UUAddressNodeNetworkService uuAddressNodeNetworkService, UUAddressDataSource uuAddressDataSource) {
        super(uuAddressNodeNetworkService, uuAddressDataSource);
    }
}
