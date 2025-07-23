package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.stereotype.Service;

@Service
public class AggregateUUPropertyValueEventListener extends AggregateNodeEventListener<UUPropertyValue> {

    public AggregateUUPropertyValueEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository) {
        super(uuStatementsDataSource, aggregateRepository);
    }
}
