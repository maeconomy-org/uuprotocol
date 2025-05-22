package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import io.recheck.uuidprotocol.common.mongodb.MongoIndexInitializer;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggregateMongoIndexInitializer extends MongoIndexInitializer {

    public AggregateMongoIndexInitializer(MongoTemplate mongoTemplate) {
        super(mongoTemplate,
                List.of(new String[] {
                        "uuid", "parents", "childs",
                        "files.uuid", "properties.uuid",
                        "properties.files.uuid", "properties.values.uuid",
                        "properties.values.files.uuid"
                }),
                AggregateEntity.class);
    }

}
