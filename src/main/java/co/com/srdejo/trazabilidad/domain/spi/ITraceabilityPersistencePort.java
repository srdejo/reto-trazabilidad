package co.com.srdejo.trazabilidad.domain.spi;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;

import java.util.List;

public interface ITraceabilityPersistencePort {

    TraceabilityModel save(TraceabilityModel traceabilityModel);
    List<TraceabilityModel> findByCustomerId(Long customerId);
    List<TraceabilityModel> findByCustomerIdAndOrderId(Long customerId, Long orderId);
    List<TraceabilityModel> findByOrderId(Long orderId);
    List<TraceabilityModel> findByNewStatus(String newStatus);
    List<TraceabilityModel> findByOrderIdInAndPreviousStatusIsNull(List<Long> orderIds);
    List<TraceabilityModel> findByOrderInitAndFinishStatus(Long orderId);

}