package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.nodenetwork.datasource.AuditDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.NodeDTO;
import io.recheck.uuidprotocol.nodenetwork.model.Node;
import io.recheck.uuidprotocol.nodenetwork.service.NodeNetworkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
public class NodeNetworkController<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeNetworkService<TNode, TNodeDTO> nodeNetworkService;
    private final AuditDataSource<TNode> dataSource;

    @GetMapping
    public ResponseEntity<Object> readAll() {
        return ResponseEntity.ok(dataSource.findAll());
    }

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> readByUUID(@PathVariable
                                                @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                        String uuid) {
        return ResponseEntity.ok(dataSource.findByUUIDAndSoftDeletedFalse(uuid));
    }

    @PostMapping
    public ResponseEntity<Object> createOrUpdate(@Valid @RequestBody TNodeDTO data, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(nodeNetworkService.createOrUpdate(data, user.getUsername()));
    }

    @DeleteMapping({"/{uuid}"})
    public ResponseEntity<Object> deleteUUObject(@PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(nodeNetworkService.softDelete(uuid, user.getUsername()));
    }


}
