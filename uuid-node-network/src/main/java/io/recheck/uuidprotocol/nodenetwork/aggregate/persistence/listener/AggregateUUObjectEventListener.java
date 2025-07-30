package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.HistoryCreate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.HistoryDelete;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUObjectUpdate;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AggregateUUObjectEventListener extends AggregateNodeEventListener<UUObject> {

    private final UUObjectDataSource uuObjectDataSource;

    private final UUObjectUpdate uuObjectUpdateOp = new UUObjectUpdate();
    private final HistoryDelete historyDeleteOp = new HistoryDelete();
    private final HistoryCreate historyCreateOp = new HistoryCreate();

    public AggregateUUObjectEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository, UUObjectDataSource uuObjectDataSource) {
        super(uuStatementsDataSource, aggregateRepository);
        this.uuObjectDataSource = uuObjectDataSource;
    }

    @Override
    public void postCreate(UUObject uuNode) {
        aggregateRepository.insertIfNotFound(uuNode);
    }

    @Override
    public void postUpdate(UUObject uuObject) {
        //update aggregate entity
        aggregateRepository.update(uuObjectUpdateOp.getQuery(uuObject), uuObjectUpdateOp.getUpdate(uuObject));

        //delete history list
        aggregateRepository.update(historyDeleteOp.getQuery(uuObject), historyDeleteOp.getUpdate());
        //find all deleted , push history list
        List<UUObject> historyNodes = uuObjectDataSource.findDeleted(uuObject.getUuid());
        for (UUObject historyNode : historyNodes) {
            aggregateRepository.update(historyCreateOp.getQuery(uuObject), historyCreateOp.getUpdate(historyNode));
        }
    }
}
