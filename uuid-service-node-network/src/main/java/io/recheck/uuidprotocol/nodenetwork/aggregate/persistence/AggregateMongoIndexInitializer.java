package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import io.recheck.uuidprotocol.common.mongodb.MongoIndexInitializer;
import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggregateMongoIndexInitializer extends MongoIndexInitializer {

    public AggregateMongoIndexInitializer(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
        return AggregateEntity.class.getSimpleName();
    }

    @Override
    protected List<String> getIndexFields() {
        return List.of(new String[] {
                "uuid", "parents", "childs",
                "address.uuid",
                "files.uuid", "properties.uuid",
                "properties.files.uuid", "properties.values.uuid",
                "properties.values.files.uuid"
        });
    }

    @Override
    protected List<String> getIndexTextFields() {
        return List.of(new String[] {
                "name", "abbreviation", "description",
                "address.fullAddress", "address.street", "address.houseNumber",
                "address.city", "address.postalCode", "address.country",
                "address.state", "address.district",
                "properties.key", "properties.version", "properties.description", "properties.type", "properties.formula",
                "properties.values.value",
                "files.fileName", "files.fileReference", "files.label",
                "properties.files.fileName", "properties.files.fileReference", "properties.files.label",
                "properties.values.files.fileName", "properties.values.files.fileReference", "properties.values.files.label"
        });
    }
}
