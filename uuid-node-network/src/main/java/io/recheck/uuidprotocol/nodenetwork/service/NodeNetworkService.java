package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.datasource.NodeDataSource;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwner;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeDataSource<TNode> dataSource;
    private final UUIDOwnerService uuidOwnerService;

    public TNode softDeleteAndCreate(TNodeDTO dto, String certFingerprint) {
        UUIDOwner UUIDOwner = uuidOwnerService.validateOwnerUUID(certFingerprint, dto.getUuid());

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(dto.getUuid());
        if (existingUUIDNode == null) {
            //create
            //validate if uuid is already used with another type of node
            if (StringUtils.hasText(UUIDOwner.getNodeType())) {
                throw new ForbiddenException("The UUID has been already used by another type of node");
            }
            else {
                uuidOwnerService.updateNodeType(UUIDOwner, dataSource.getType().getSimpleName());
            }
        }
        else {
            //update
            dataSource.softDeleteAudit(existingUUIDNode, certFingerprint);
        }

        return dataSource.createOrUpdateAudit(dto.build(), certFingerprint);
    }

    public TNode softDelete(String uuid, String certFingerprint) {
        uuidOwnerService.validateOwnerUUID(certFingerprint, uuid);

        TNode existingUUIDNode = dataSource.findByUUIDAndSoftDeletedFalse(uuid);
        if (existingUUIDNode == null) {
            throw new NotFoundException("Not found for soft delete");
        }

        return dataSource.softDeleteAudit(existingUUIDNode, certFingerprint);
    }

}
