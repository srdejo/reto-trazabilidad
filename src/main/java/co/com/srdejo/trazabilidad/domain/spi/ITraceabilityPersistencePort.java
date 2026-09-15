package co.com.srdejo.trazabilidad.domain.spi;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;

import java.util.List;

public interface ITraceabilityPersistencePort {

    TraceabilityModel save(TraceabilityModel traceabilityModel);
    List<TraceabilityModel> findByCustomerId(Long customerId);
    List<TraceabilityModel> findByCustomerIdAndOrderId(Long customerId, Long orderId);

}