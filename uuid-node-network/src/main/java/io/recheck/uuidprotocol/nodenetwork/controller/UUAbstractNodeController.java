package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.dto.NodeFindDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.service.NodeNetworkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
public class UUAbstractNodeController<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeNetworkService<TNode, TNodeDTO> nodeNetworkService;
    private final NodeDataSource<TNode> dataSource;

    @PostMapping
    public ResponseEntity<Object> softDeleteAndCreate(@Valid @RequestBody TNodeDTO data, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(nodeNetworkService.softDeleteAndCreate(data, user.getCertFingerprint()));
    }

    @DeleteMapping({"/{uuid}"})
    public ResponseEntity<Object> softDelete(@PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(nodeNetworkService.softDelete(uuid, user.getCertFingerprint()));
    }

    @GetMapping
    public ResponseEntity<Object> findBySoftDeleted(@Valid NodeFindDTO nodeFindDTO) {
        return ResponseEntity.ok(dataSource.where(nodeFindDTO));
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findBySoftDeletedOwn(@Valid NodeFindDTO nodeFindDTO, @AuthenticationPrincipal X509UserDetails user) {
        nodeFindDTO.setCreatedBy(user.getCertFingerprint());
        return ResponseEntity.ok(dataSource.where(nodeFindDTO));
    }

}
