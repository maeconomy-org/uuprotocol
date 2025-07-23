package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
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
            "address.uuid",
            "files.uuid", "properties.uuid",
            "properties.files.uuid", "properties.values.uuid",
            "properties.values.files.uuid"
    });

    private final List<String> indexTextFields = List.of(new String[] {
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

    @PostConstruct
    public void initIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(AggregateEntity.class);

        for (String indexField : indexFields) {
            indexOps.ensureIndex(new Index().on(indexField, Sort.Direction.ASC));
        }

        MongoCollection<Document> collection = mongoTemplate.getCollection(AggregateEntity.class.getSimpleName());
        Document index = new Document();
        for (String indexTextField : indexTextFields) {
            index.append(indexTextField, "text");
        }
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.name("searchTextIndex");
        collection.createIndex(index, indexOptions);
    }

}
