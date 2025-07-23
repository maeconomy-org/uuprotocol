package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AggregateMongoIndexInitializer {

    private final MongoTemplate mongoTemplate;

    private final List<String> indexFields = List.of(new String[] {
            "uuid", "parents", "childs",
            "files.uuid", "properties.uuid",
            "properties.files.uuid", "properties.values.uuid",
            "properties.values.files.uuid"
    });

    @PostConstruct
    public void initIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(AggregateEntity.class);

        for (String indexField : indexFields) {
            indexOps.ensureIndex(new Index().on(indexField, Sort.Direction.ASC));
        }
    }

}
