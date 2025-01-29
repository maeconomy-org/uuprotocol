package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.datasource.NodeDataSource;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeDataSource<TNode> dataSource;
    private final OwnerUUIDService ownerUUIDService;

    public TNode softDeleteAndCreate(TNodeDTO dto, String ownerCertFingerprint) {
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, dto.getUuid());

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(dto.getUuid());
        if (existingUUIDNode != null) {
            dataSource.softDeleteAudit(existingUUIDNode, ownerCertFingerprint);
        }

        return dataSource.createOrUpdateAudit(dto.build(), ownerCertFingerprint);
    }

    public TNode softDelete(String uuid, String ownerCertFingerprint) {
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, uuid);

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(uuid);
        if (existingUUIDNode == null) {
            throw new NotFoundException("Not found for soft delete");
        }

        return dataSource.softDeleteAudit(existingUUIDNode, ownerCertFingerprint);
    }

}
