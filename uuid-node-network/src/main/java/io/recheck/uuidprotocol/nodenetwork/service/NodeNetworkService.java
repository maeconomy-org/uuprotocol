package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeDataSource<TNode> dataSource;
    private final UUIDOwnerService uuidOwnerService;
    private final AggregateService aggregateService;

    public TNode softDeleteAndCreate(TNodeDTO dto, String certFingerprint) {
        validateAndUpdateType(dto, certFingerprint);

        TNode lastUpdated = dataSource.findLastUpdated(dto.getUuid());
        if (lastUpdated != null) {
            TNode lastDeleted = dataSource.findLastDeleted(dto.getUuid());
            if (lastDeleted == null || lastUpdated.getLastUpdatedAt().isAfter(lastDeleted.getSoftDeletedAt())) {
                dataSource.softDeleteAudit(lastUpdated, certFingerprint);
            }
        }

        TNode node = dataSource.createOrUpdateAudit(dto.build(), certFingerprint);
        aggregateService.updateNode(node);
        return node;
    }

    public TNode softDelete(String uuid, String certFingerprint) {
        uuidOwnerService.validateOwnerUUID(certFingerprint, uuid);

        TNode existingUUIDNode = dataSource.findByUUID(uuid);
        if (existingUUIDNode == null) {
            throw new NotFoundException("Not found for soft delete");
        }

        TNode lastUpdated = dataSource.findLastUpdated(uuid);
        TNode lastDeleted = dataSource.findLastDeleted(uuid);
        if (lastUpdated != null) {
            if (lastDeleted == null || lastUpdated.getLastUpdatedAt().isAfter(lastDeleted.getSoftDeletedAt())) {
                lastDeleted = dataSource.softDeleteAudit(lastUpdated, certFingerprint);
                aggregateService.updateNode(lastDeleted);
            }
        }
        return lastDeleted;
    }

    private void validateAndUpdateType(TNodeDTO dto, String certFingerprint) {
        UUIDOwner uuidOwner = uuidOwnerService.validateOwnerUUID(certFingerprint, dto.getUuid());

        //if dto.getUuid() is another type then executing dataSource.findByUUID will not find node
        TNode existingUUIDNode = dataSource.findByUUID(dto.getUuid());
        if (existingUUIDNode == null) {
            //create
            //validate if uuid is already used with another type of node
            if (StringUtils.hasText(uuidOwner.getNodeType())) {
                throw new ForbiddenException("The UUID has been already used by another type of node");
            }
            else {
                uuidOwnerService.updateNodeType(uuidOwner.getUuid(), dataSource.getCollectionType().getSimpleName());
            }
        }
    }

}
