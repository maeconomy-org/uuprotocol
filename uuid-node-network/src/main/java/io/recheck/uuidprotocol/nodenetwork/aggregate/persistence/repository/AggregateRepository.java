package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository;

import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregateRepository extends MongoRepository<AggregateEntity, ObjectId> {

    @Query("{ $or: [ " +
            "{ 'uuid': ?0 }, " +
            "{ 'parents': ?0 }, " +
            "{ 'childs': ?0 }, " +
            "{ 'files.uuid': ?0 }, " +
            "{ 'properties.uuid': ?0 }, " +
            "{ 'properties.files.uuid': ?0 }, " +
            "{ 'properties.values.uuid': ?0 }, " +
            "{ 'properties.values.files.uuid': ?0 } " +
            "] }")
    List<AggregateEntity> findByAnyUuid(String uuid);

}
