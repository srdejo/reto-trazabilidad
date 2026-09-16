package co.com.srdejo.trazabilidad.application.handler.impl;

import co.com.srdejo.trazabilidad.application.dto.request.TraceabilityRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.EmployeeRankingResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.OrderDurationResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;
import co.com.srdejo.trazabilidad.application.mapper.ITraceabilityRequestMapper;
import co.com.srdejo.trazabilidad.application.mapper.ITraceabilityResponseMapper;
import co.com.srdejo.trazabilidad.domain.api.ITraceabilityServicePort;
import co.com.srdejo.trazabilidad.domain.model.EmployeeRankingModel;
import co.com.srdejo.trazabilidad.domain.model.OrderDurationModel;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityHandlerTest {

    @Mock
    private ITraceabilityServicePort traceabilityServicePort;

    @Mock
    private ITraceabilityRequestMapper traceabilityRequestMapper;

    @Mock
    private ITraceabilityResponseMapper traceabilityResponseMapper;

    private TraceabilityHandler traceabilityHandler;

    @BeforeEach
    void setUp() {
        traceabilityHandler = new TraceabilityHandler(
                traceabilityServicePort, traceabilityRequestMapper, traceabilityResponseMapper);
    }

    @Test
    void registerStatusChange_mapsRequestDelegatesToServicePortAndMapsResponse() {
        TraceabilityRequestDto requestDto = new TraceabilityRequestDto(
                123L, 15L, "customer@mail.com", "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityModel mappedModel = new TraceabilityModel(null, 123L, 15L, "customer@mail.com",
                null, "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityModel savedModel = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityResponseDto expectedResponse = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", savedModel.getDate(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityRequestMapper.toModel(requestDto)).thenReturn(mappedModel);
        when(traceabilityServicePort.registerStatusChange(mappedModel)).thenReturn(savedModel);
        when(traceabilityResponseMapper.toResponse(savedModel)).thenReturn(expectedResponse);

        TraceabilityResponseDto result = traceabilityHandler.registerStatusChange(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(traceabilityServicePort).registerStatusChange(mappedModel);
    }

    @Test
    void getHistoryByCustomer_delegatesToServicePortAndMapsResponseList() {
        TraceabilityModel model = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", model.getDate(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityServicePort.getHistoryByCustomer(null)).thenReturn(List.of(model));
        when(traceabilityResponseMapper.toResponseList(List.of(model))).thenReturn(List.of(responseDto));

        List<TraceabilityResponseDto> result = traceabilityHandler.getHistoryByCustomer(null);

        assertThat(result).containsExactly(responseDto);
    }

    @Test
    void getHistoryByCustomer_withOrderId_delegatesToServicePortWithOrderId() {
        TraceabilityModel model = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", model.getDate(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityServicePort.getHistoryByCustomer(123L)).thenReturn(List.of(model));
        when(traceabilityResponseMapper.toResponseList(List.of(model))).thenReturn(List.of(responseDto));

        List<TraceabilityResponseDto> result = traceabilityHandler.getHistoryByCustomer(123L);

        assertThat(result).containsExactly(responseDto);
        verify(traceabilityServicePort).getHistoryByCustomer(123L);
    }

    @Test
    void getOrderDuration_delegatesToServicePortAndMapsResponse() {
        OrderDurationModel model = new OrderDurationModel(123L, LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now(), 1800L);
        OrderDurationResponseDto responseDto = new OrderDurationResponseDto(123L, model.getStartDate(),
                model.getEndDate(), 1800L);
        when(traceabilityServicePort.getOrderDuration(123L)).thenReturn(model);
        when(traceabilityResponseMapper.toDurationResponse(model)).thenReturn(responseDto);

        OrderDurationResponseDto result = traceabilityHandler.getOrderDuration(123L);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void getEmployeeRanking_delegatesToServicePortAndMapsResponseList() {
        EmployeeRankingModel model = new EmployeeRankingModel(7L, "employee@mail.com", 3, 1200.0);
        EmployeeRankingResponseDto responseDto = new EmployeeRankingResponseDto(7L, "employee@mail.com", 3, 1200.0);
        when(traceabilityServicePort.getEmployeeRanking()).thenReturn(List.of(model));
        when(traceabilityResponseMapper.toRankingResponseList(List.of(model))).thenReturn(List.of(responseDto));

        List<EmployeeRankingResponseDto> result = traceabilityHandler.getEmployeeRanking();

        assertThat(result).containsExactly(responseDto);
    }
}
