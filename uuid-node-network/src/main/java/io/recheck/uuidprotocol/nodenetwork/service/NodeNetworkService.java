package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.nodenetwork.datasource.AuditDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.NodeDTO;
import io.recheck.uuidprotocol.nodenetwork.model.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final AuditDataSource<TNode> dataSource;
    private final ClientUUIDService clientUUIDService;

    public TNode createOrUpdate(TNodeDTO dto, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, dto.getUuid());
        return dataSource.createOrUpdateAudit(dto.build(), clientCertFingerprint);
    }

    public TNode softDelete(String uuid, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuid);
        return dataSource.softDeleteAudit(uuid, clientCertFingerprint);
    }

}
