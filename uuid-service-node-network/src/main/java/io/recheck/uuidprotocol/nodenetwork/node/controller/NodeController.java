package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
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
    public ResponseEntity<Object> softDeleteAndCreate(@AuthenticationPrincipal UserDetailsCustom user,
                                                      @Valid @RequestBody TNodeDTO data) {
        return ResponseEntity.ok(nodeNetworkService.softDeleteAndCreate(data, user));
    }

    @DeleteMapping({"/{uuid}"})
    public ResponseEntity<Object> softDelete(@AuthenticationPrincipal UserDetailsCustom user,
                                             @PathVariable @Pattern(regexp = UUIDRegExp.re) String uuid) {
        return ResponseEntity.ok(nodeNetworkService.softDelete(uuid, user));
    }

    @GetMapping
    public ResponseEntity<Object> findByDTOAndOrderByLastUpdatedAt(@AuthenticationPrincipal UserDetailsCustom user,
                                                                   @Valid NodeFindDTO nodeFindDTO) {
        return ResponseEntity.ok(dataSource.findByDTOAndOrderByLastUpdatedAt(user, nodeFindDTO));
    }

}
