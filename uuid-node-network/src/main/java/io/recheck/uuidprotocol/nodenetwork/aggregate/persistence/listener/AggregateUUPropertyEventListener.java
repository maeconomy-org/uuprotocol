package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.stereotype.Service;

@Service
public class AggregateUUPropertyEventListener extends AggregateNodeEventListener<UUProperty> {

    public AggregateUUPropertyEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository) {
        super(uuStatementsDataSource, aggregateRepository);
    }
}
