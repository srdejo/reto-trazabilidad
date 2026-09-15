package co.com.srdejo.trazabilidad.domain.api;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;

import java.util.List;

public interface ITraceabilityServicePort {
    TraceabilityModel registerStatusChange(TraceabilityModel traceabilityModel);
    List<TraceabilityModel> getHistoryByCustomer(Long orderId);
}