package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.dto.NodeFindDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.NodeNetworkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
public class NodeController<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    protected final NodeNetworkService<TNode, TNodeDTO> nodeNetworkService;
    protected final NodeDataSource<TNode> dataSource;

    @PostMapping
    public ResponseEntity<Object> softDeleteAndCreate(@Valid @RequestBody TNodeDTO data, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(nodeNetworkService.softDeleteAndCreate(data, user.getCertificate().getCertificateSha256()));
    }

    @DeleteMapping({"/{uuid}"})
    public ResponseEntity<Object> softDelete(@PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(nodeNetworkService.softDelete(uuid, user.getCertificate().getCertificateSha256()));
    }

    @GetMapping
    public ResponseEntity<Object> findByDTOAndOrderByLastUpdatedAt(@Valid NodeFindDTO nodeFindDTO) {
        return ResponseEntity.ok(dataSource.findByDTOAndOrderByLastUpdatedAt(nodeFindDTO));
    }

}
