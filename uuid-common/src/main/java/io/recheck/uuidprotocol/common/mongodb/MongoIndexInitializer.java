package io.recheck.uuidprotocol.common.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
public abstract class MongoIndexInitializer {

    private final MongoTemplate mongoTemplate;
    private final List<String> indexFields;
    private final Class<?> entityClass;

    @PostConstruct
    public void initIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(entityClass);

        for (String indexField : indexFields) {
            indexOps.ensureIndex(new Index().on(indexField, Sort.Direction.ASC));
        }
    }

}
