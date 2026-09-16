package co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository;

import co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity.TraceabilityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ITraceabilityRepository extends MongoRepository<TraceabilityEntity,
        String> {

    List<TraceabilityEntity> findByCustomerIdOrderByDateAsc(Long customerId);
    List<TraceabilityEntity> findByCustomerIdAndOrderIdOrderByDateAsc(Long customerId, Long orderId);
    List<TraceabilityEntity> findByOrderId(Long orderId);
    List<TraceabilityEntity> findByNewStatus(String newStatus);
    List<TraceabilityEntity> findByOrderIdInAndPreviousStatusIsNull(List<Long> orderIds);
    List<TraceabilityEntity> findByOrderIdAndNewStatusOrOrderIdAndPreviousStatusIsNull(
            Long orderId, String newStatus, Long orderIdForPreviousStatus);

}
