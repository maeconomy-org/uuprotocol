package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.UUAddress;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.stereotype.Service;

@Service
public class AggregateUUAddressEventListener extends AggregateNodeEventListener<UUAddress> {

    public AggregateUUAddressEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository) {
        super(uuStatementsDataSource, aggregateRepository);
    }
}
