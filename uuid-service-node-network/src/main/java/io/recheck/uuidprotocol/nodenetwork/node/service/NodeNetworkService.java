package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.dto.NodeDTO;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecordMeta;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateNodeEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.registrar.UUIDRegistrarService;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class NodeNetworkService<TNode extends Node, TNodeDTO extends NodeDTO<TNode>> {

    protected final NodeDataSource<TNode> dataSource;
    protected final AggregateNodeEventListener<TNode> aggregateNodeEventListener;
    protected final UUIDRegistrarService uuidRegistrarService;

    public TNode softDeleteAndCreate(TNodeDTO dto, UserDetailsCustom user) {
        uuidRegistrarService.authorize(user.getUserUUID(), dto.getUuid());

        validateAndUpdateType(dto, user);

        TNode last = dataSource.findLastUpdated(dto.getUuid());
        if (last != null && !last.getSoftDeleted()) {
            dataSource.softDelete(last, user);
        }

        TNode node = dataSource.createAudit(dto.build(), user);
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

    public TNode softDelete(String uuid, UserDetailsCustom user) {
        uuidRegistrarService.authorize(user.getUserUUID(), uuid);

        TNode last = dataSource.findLastUpdated(uuid);
        if (last == null) {
            throw new NotFoundException("Not found for soft delete");
        }
        else if (!last.getSoftDeleted()) {
            last = dataSource.softDelete(last, user);
            aggregateNodeEventListener.postSoftDelete(last);
        }

        return last;
    }

    private void validateAndUpdateType(TNodeDTO dto, UserDetailsCustom user) {
        UUIDRecord uuidRecord = uuidRegistrarService.findByUUID(dto.getUuid());
        if (uuidRecord != null) {
            if (uuidRecord.getUuidRecordMeta() != null) {
                if (!uuidRecord.getUuidRecordMeta().getNodeType().equals(dataSource.getCollectionType().getSimpleName())) {
                    throw new ForbiddenException("The UUID has been already used by another type of node");
                }
            }
            else {
                UUIDRecordMeta uuidRecordMeta = new UUIDRecordMeta();
                uuidRecordMeta.setNodeType(dataSource.getCollectionType().getSimpleName());
                uuidRegistrarService.updateUUIDRecordMeta(dto.getUuid(), uuidRecordMeta);
            }
        }
    }

}
