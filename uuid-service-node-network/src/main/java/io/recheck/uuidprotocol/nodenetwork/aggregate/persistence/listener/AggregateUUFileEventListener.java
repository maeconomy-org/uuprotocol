package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import org.springframework.stereotype.Service;

@Service
public class AggregateUUFileEventListener extends AggregateNodeEventListener<UUFile> {

    public AggregateUUFileEventListener(UUStatementsDataSource uuStatementsDataSource, AggregateRepository aggregateRepository) {
        super(uuStatementsDataSource, aggregateRepository);
    }
}
