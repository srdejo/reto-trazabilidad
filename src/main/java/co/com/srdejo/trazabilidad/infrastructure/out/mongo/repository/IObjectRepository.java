package co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository;

import co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity.ObjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IObjectRepository extends MongoRepository<ObjectEntity, Long> {

}
