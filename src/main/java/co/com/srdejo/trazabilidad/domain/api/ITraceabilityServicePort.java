package co.com.srdejo.trazabilidad.domain.api;

import co.com.srdejo.trazabilidad.domain.model.EmployeeRankingModel;
import co.com.srdejo.trazabilidad.domain.model.OrderDurationModel;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;

import java.util.List;

public interface ITraceabilityServicePort {
    TraceabilityModel registerStatusChange(TraceabilityModel traceabilityModel);
    List<TraceabilityModel> getHistoryByCustomer(Long orderId);
    OrderDurationModel getOrderDuration(Long orderId);
    List<EmployeeRankingModel> getEmployeeRanking();
}