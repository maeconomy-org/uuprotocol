package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateFindDTO;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateRepositoryTemplate {

    private final MongoTemplate mongoTemplate;

    public Page<AggregateEntity> find(AggregateFindDTO aggregateFindDTO) {
        /*
        Document projectStage = new Document("$project", new Document()
                .append("uuid", 1)
                .append("name", 1)
                .append("version", 1)
                .append("lastUpdatedAt", 1)
                .append("files", 1)
                .append("properties", 1)
                .append("deepLastUpdatedAt", new Document("$max", List.of(
                        "$lastUpdatedAt",
                        new Document("$max", "$files.lastUpdatedAt"),
                        new Document("$max", "$properties.lastUpdatedAt"),
                        new Document("$max", "$properties.values.lastUpdatedAt"),
                        new Document("$max", "$properties.files.lastUpdatedAt"),
                        new Document("$max", "$properties.values.files.lastUpdatedAt")
                )))
        );
         */

        Pageable pageable = PageRequest.of(aggregateFindDTO.getPage(), aggregateFindDTO.getSize());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "$createdAt")),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize())
        );

        if (!aggregateFindDTO.getHasHistory()) {
            aggregation.getPipeline().add(Aggregation.project().andExclude("history"));
        }
        if (StringUtils.hasText(aggregateFindDTO.getCreatedBy())) {
            aggregation.getPipeline().add(Aggregation.match(Criteria.where("createdBy").is(aggregateFindDTO.getCreatedBy())));
        }

        AggregationResults<AggregateEntity> results = mongoTemplate.aggregate(
                aggregation, AggregateEntity.class.getSimpleName(), AggregateEntity.class
        );

        long total = mongoTemplate.count(new Query(), AggregateEntity.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }

    public void insertIfNotFound(UUObject uuObject) {
        List<AggregateEntity> aggregateEntities = mongoTemplate.find(new Query(Criteria.where("uuid").is(uuObject.getUuid())), AggregateEntity.class);
        if (aggregateEntities.isEmpty()) {
            mongoTemplate.getCollection(AggregateEntity.class.getSimpleName()).insertOne(MongoUtils.convertToDocument(AggregateEntity.buildFromUUObject(uuObject)));
        }
    }

    public <TNode extends Node> void update(AbstractOperation operation, TNode uuNode) {
        mongoTemplate.updateMulti(operation.getQuery(uuNode), operation.getUpdate(uuNode), AggregateEntity.class);
    }

    public <TNode extends Node, VNode extends Node> void update(AbstractOperation operation, TNode parentNode, VNode childNode) {
        mongoTemplate.updateMulti(operation.getQuery(parentNode), operation.getUpdate(childNode, parentNode), AggregateEntity.class);
    }


}
