package co.com.srdejo.trazabilidad.application.handler.impl;

import co.com.srdejo.trazabilidad.application.dto.request.TraceabilityRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.EmployeeRankingResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.OrderDurationResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;
import co.com.srdejo.trazabilidad.application.handler.ITraceabilityHandler;
import co.com.srdejo.trazabilidad.application.mapper.ITraceabilityRequestMapper;
import co.com.srdejo.trazabilidad.application.mapper.ITraceabilityResponseMapper;
import co.com.srdejo.trazabilidad.domain.api.ITraceabilityServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TraceabilityHandler implements ITraceabilityHandler {

    private final ITraceabilityServicePort traceabilityServicePort;
    private final ITraceabilityRequestMapper traceabilityRequestMapper;
    private final ITraceabilityResponseMapper traceabilityResponseMapper;

    @Override
    public TraceabilityResponseDto registerStatusChange(TraceabilityRequestDto traceabilityRequestDto) {
        log.debug("Received traceability event for order {}", traceabilityRequestDto.orderId());
        var model = traceabilityRequestMapper.toModel(traceabilityRequestDto);
        var saved = traceabilityServicePort.registerStatusChange(model);
        return traceabilityResponseMapper.toResponse(saved);
    }

    @Override
    public List<TraceabilityResponseDto> getHistoryByCustomer(Long orderId) {
        log.debug("Fetching traceability history, orderId filter={}", orderId);
        var models = traceabilityServicePort.getHistoryByCustomer(orderId);
        return traceabilityResponseMapper.toResponseList(models);
    }

    @Override
    public OrderDurationResponseDto getOrderDuration(Long orderId) {
        log.debug("Fetching order duration for order {}", orderId);
        var model = traceabilityServicePort.getOrderDuration(orderId);
        return traceabilityResponseMapper.toDurationResponse(model);
    }

    @Override
    public List<EmployeeRankingResponseDto> getEmployeeRanking() {
        log.debug("Fetching employee ranking");
        var models = traceabilityServicePort.getEmployeeRanking();
        return traceabilityResponseMapper.toRankingResponseList(models);
    }
}