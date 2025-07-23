package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.common.mongodb.query.UpdateArrayDoc;
import io.recheck.uuidprotocol.common.mongodb.query.UpdateDoc;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AggregateUUObjectEventListener extends AggregateNodeEventListener<UUObject> {

    private final UUObjectDataSource uuObjectDataSource;

    private final UpdateDoc updateUUObject = new UpdateDoc("");
    private final UpdateDoc updateUUObjectHistory = new UpdateDoc("history");
    private final UpdateArrayDoc updateArrayUUObjectHistory = new UpdateArrayDoc("history", "uuid", "uuid");

    public AggregateUUObjectEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository, UUObjectDataSource uuObjectDataSource) {
        super(uuStatementsDataSource, aggregateRepository);
        this.uuObjectDataSource = uuObjectDataSource;
    }

    @Override
    public void postUpdate(UUObject uuObject) {
        aggregateRepository.insertIfNotFound(uuObject);

        Query query = new Query(Criteria.where("uuid").is(uuObject.getUuid()));
        aggregateRepository.update(query, updateUUObject.setDoc(uuObject));

        //delete history list
        aggregateRepository.update(query, updateUUObjectHistory.unsetDoc());
        //find all deleted , push history list
        List<UUObject> historyNodes = uuObjectDataSource.findDeleted(uuObject.getUuid());
        for (UUObject historyNode : historyNodes) {
            aggregateRepository.update(query, updateArrayUUObjectHistory.pushArrayDoc(uuObject.getUuid(), historyNode));
        }
    }
}
