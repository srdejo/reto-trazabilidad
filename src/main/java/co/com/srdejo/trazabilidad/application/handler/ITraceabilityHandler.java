package co.com.srdejo.trazabilidad.application.handler;

import co.com.srdejo.trazabilidad.application.dto.request.TraceabilityRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;

import java.util.List;

public interface ITraceabilityHandler {
    TraceabilityResponseDto registerStatusChange(TraceabilityRequestDto traceabilityRequestDto);
    List<TraceabilityResponseDto> getHistoryByCustomer(Long orderId);
}