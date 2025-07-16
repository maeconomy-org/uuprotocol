package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateFindDTO;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractOperation;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateRepositoryTemplate {

    private final MongoTemplate mongoTemplate;

    public List<AggregateEntity> findByAnyUuid(String uuid) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("uuid").is(uuid),
                Criteria.where("parents").is(uuid),
                Criteria.where("childs").is(uuid),
                Criteria.where("files.uuid").is(uuid),
                Criteria.where("properties.uuid").is(uuid),
                Criteria.where("properties.files.uuid").is(uuid),
                Criteria.where("properties.values.uuid").is(uuid),
                Criteria.where("properties.values.files.uuid").is(uuid)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, AggregateEntity.class);
    }

    public Page<AggregateEntity> find(AggregateFindDTO aggregateFindDTO) {
        List<AggregationOperation> stages = new ArrayList<>();

        if (Boolean.TRUE.equals(aggregateFindDTO.getHasChildrenFull())) {
            Document lookupChildrenStage = new Document("$lookup", new Document()
                    .append("from", "AggregateEntity")
                    .append("localField", "children")
                    .append("foreignField", "uuid")
                    .append("as", "childrenFull")
            );
            stages.add(ctx -> lookupChildrenStage);
        }
        if (Boolean.FALSE.equals(aggregateFindDTO.getHasHistory())) {
            stages.add(Aggregation.project().andExclude("history"));
        }
        if (StringUtils.hasText(aggregateFindDTO.getCreatedBy())) {
            stages.add(Aggregation.match(Criteria.where("createdBy").is(aggregateFindDTO.getCreatedBy())));
        }
        stages.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "$createdAt")));

        //get total
        Aggregation aggregation = Aggregation.newAggregation(stages);
        aggregation.getPipeline().add(Aggregation.count().as("total"));
        AggregationResults<Document> countResults = mongoTemplate.aggregate(aggregation, AggregateEntity.class.getSimpleName(), Document.class);
        int total = countResults.getMappedResults().stream()
                .findFirst()
                .map(d -> d.getInteger("total"))
                .orElse(0);

        //get results
        aggregation = Aggregation.newAggregation(stages);
        Pageable pageable = PageRequest.of(aggregateFindDTO.getPage(), aggregateFindDTO.getSize());
        aggregation.getPipeline()
                .add(Aggregation.skip(pageable.getOffset()))
                .add(Aggregation.limit(pageable.getPageSize()));
        AggregationResults<AggregateEntity> results = mongoTemplate.aggregate(aggregation, AggregateEntity.class.getSimpleName(), AggregateEntity.class);

        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }

    public void saveAll(List<AggregateEntity> aggregateEntityList) {
        mongoTemplate.insertAll(aggregateEntityList);
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
