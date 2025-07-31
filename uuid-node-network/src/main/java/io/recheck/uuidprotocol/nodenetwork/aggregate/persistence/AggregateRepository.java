package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import io.recheck.uuidprotocol.domain.aggregate.dto.AggregateFindDTO;
import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateRepository {

    private final MongoTemplate mongoTemplate;

    public List<AggregateEntity> findByAnyUuid(String uuid) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("uuid").is(uuid),
                Criteria.where("parents").is(uuid),
                Criteria.where("childs").is(uuid),
                Criteria.where("address.uuid").is(uuid),
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

        if (StringUtils.hasText(aggregateFindDTO.getSearchTerm())) {
            Document match = new Document("$match", new Document("$text", new Document("$search", aggregateFindDTO.getSearchTerm())));
            stages.add(ctx -> match);

            // Include score for sort
            Document addScore = new Document("$addFields", new Document("score", new Document("$meta", "textScore")));
            stages.add(ctx -> addScore);

            // Sort by score DESC
            stages.add(Aggregation.sort(Sort.by(Sort.Order.desc("score"))));
        }

        if (aggregateFindDTO.getHasParentUUIDFilter()==true) {
            if (StringUtils.hasText(aggregateFindDTO.getParentUUID())) {
                stages.add(Aggregation.match(Criteria.where("parents").is(aggregateFindDTO.getParentUUID())));
            }
            else {
                stages.add(context -> new Document("$addFields", new Document("parentsCount", new Document("$size", "$parents"))));
                stages.add(context -> new Document("$match", new Document("parentsCount", 0)));
            }
        }


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
            stages.add(new AddFieldsOperation("history", new ArrayList<>()));
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

    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, AggregateEntity.class);
    }


}
