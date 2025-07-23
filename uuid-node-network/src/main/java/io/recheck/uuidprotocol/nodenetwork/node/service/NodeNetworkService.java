package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateNodeEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    private final NodeDataSource<TNode> dataSource;
    private final AggregateNodeEventListener<TNode> aggregateNodeEventListener;
    private final UUIDOwnerService uuidOwnerService;

    public TNode softDeleteAndCreate(TNodeDTO dto, String certFingerprint) {
        validateAndUpdateType(dto, certFingerprint);

        TNode last = dataSource.findLast(dto.getUuid());
        if (last != null && !last.getSoftDeleted()) {
            dataSource.softDeleteAudit(last, certFingerprint);
        }

        TNode node = dataSource.createAudit(dto.build(), certFingerprint);
        if (aggregateNodeEventListener != null) {
            if (last == null) {
                aggregateNodeEventListener.postCreate(node);
            }
            else {
                aggregateNodeEventListener.postUpdate(node);
            }
        }
        return node;
    }

    public TNode softDelete(String uuid, String certFingerprint) {
        uuidOwnerService.validateOwnerUUID(certFingerprint, uuid);

        TNode last = dataSource.findLast(uuid);
        if (last == null) {
            throw new NotFoundException("Not found for soft delete");
        }
        else if (!last.getSoftDeleted()) {
            last = dataSource.softDeleteAudit(last, certFingerprint);
            aggregateNodeEventListener.postSoftDelete(last);
        }

        return last;
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
