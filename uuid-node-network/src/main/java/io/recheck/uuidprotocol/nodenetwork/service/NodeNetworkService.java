package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
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

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(dto.getUuid());
        if (existingUUIDNode != null) {
            dataSource.softDeleteAudit(existingUUIDNode.getId(), clientCertFingerprint);
        }

        return dataSource.createOrUpdateAudit(dto.build(), clientCertFingerprint);
    }

    public TNode softDelete(String uuid, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuid);

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(uuid);
        if (existingUUIDNode == null) {
            throw new NotFoundException("Not found for soft delete");
        }

        return dataSource.softDeleteAudit(existingUUIDNode.getId(), clientCertFingerprint);
    }

}
