package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.domain.node.datasource.NodeDataSource;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
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
    private final NodeDataSource<TNode> dataSource;

    @GetMapping
    public ResponseEntity<Object> findBySoftDeleted(@RequestParam(required = false) Boolean softDeleted) {
        return ResponseEntity.ok(dataSource.findByOrFindAll(null, softDeleted));
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findBySoftDeletedOwn(@RequestParam(required = false) Boolean softDeleted, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(dataSource.findByOrFindAll(user.getUsername(), softDeleted));
    }


    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByUUIDAndSoftDeleted(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid,
                                                            @RequestParam(required = false) Boolean softDeleted) {
        return ResponseEntity.ok(dataSource.findByOrFindAll(uuid,null, softDeleted));
    }

    @GetMapping({"/{uuid}/own"})
    public ResponseEntity<Object> findByUUIDAndSoftDeletedOwn(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid,
                                                                @RequestParam(required = false) Boolean softDeleted,
                                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(dataSource.findByOrFindAll(uuid,user.getUsername(), softDeleted));
    }



    @PostMapping
    public ResponseEntity<Object> softDeleteAndCreate(@Valid @RequestBody TNodeDTO data, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(nodeNetworkService.softDeleteAndCreate(data, user.getUsername()));
    }

    @DeleteMapping({"/{uuid}"})
    public ResponseEntity<Object> softDelete(@PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(nodeNetworkService.softDelete(uuid, user.getUsername()));
    }


}
