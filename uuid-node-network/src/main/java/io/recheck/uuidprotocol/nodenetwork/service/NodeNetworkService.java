package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.datasource.NodeDataSource;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUID;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeDataSource<TNode> dataSource;
    private final OwnerUUIDService ownerUUIDService;

    public TNode softDeleteAndCreate(TNodeDTO dto, String ownerCertFingerprint) {
        OwnerUUID ownerUUID = ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, dto.getUuid());

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(dto.getUuid());
        if (existingUUIDNode == null) {
            //create
            //validate if uuid is already used with another type of node
            if (StringUtils.hasText(ownerUUID.getNodeType())) {
                throw new ForbiddenException("The UUID has been already used by another type of node");
            }
            else {
                ownerUUIDService.updateNodeType(ownerUUID, dataSource.getType().getSimpleName());
            }
        }
        else {
            //update
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
