package io.recheck.uuidprotocol.common.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;

@RequiredArgsConstructor
public abstract class MongoIndexInitializer {

    private final MongoTemplate mongoTemplate;

    protected abstract String getCollectionName();
    protected abstract List<String> getIndexFields();
    protected abstract List<String> getIndexTextFields();

    public void initIndexes() {
        List<String> indexFields = getIndexFields();
        List<String> indexTextFields = getIndexTextFields();

        IndexOperations indexOps = mongoTemplate.indexOps(getCollectionName());

        for (String indexField : indexFields) {
            indexOps.ensureIndex(new Index().on(indexField, Sort.Direction.ASC));
        }

        MongoCollection<Document> collection = mongoTemplate.getCollection(getCollectionName());
        Document index = new Document();
        for (String indexTextField : indexTextFields) {
            index.append(indexTextField, "text");
        }
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.name("searchTextIndex");
        collection.createIndex(index, indexOptions);
    }
}
