package co.com.srdejo.trazabilidad.infrastructure.input.rest;

import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;
import co.com.srdejo.trazabilidad.application.handler.ITraceabilityHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TraceabilityRestController.class)
class TraceabilityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITraceabilityHandler traceabilityHandler;

    private String validRequestJson() {
        return """
                {
                  "orderId": 123,
                  "customerId": 15,
                  "customerEmail": "customer@mail.com",
                  "previousStatus": "PENDING",
                  "newStatus": "IN_PREPARATION",
                  "employeeId": 7,
                  "employeeEmail": "employee@mail.com"
                }
                """;
    }

    @Test
    void registerStatusChange_withValidPayload_returns201AndInvokesHandler() throws Exception {
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityHandler.registerStatusChange(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.orderId").value(123));

        verify(traceabilityHandler).registerStatusChange(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"orderId", "customerId"})
    void registerStatusChange_withMissingRequiredNumber_returns400AndHandlerIsNeverCalled(String field) throws Exception {
        String payload = validRequestJson().replaceFirst("\"" + field + "\":\\s*\\d+,?\\s*", "");

        mockMvc.perform(post("/api/v1/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traceabilityHandler);
    }

    @Test
    void registerStatusChange_withMissingEmployeeId_returns201AndInvokesHandler() throws Exception {
        // employeeId is optional: a status change may not be caused by an employee (e.g. order creation or a customer cancellation).
        String payload = validRequestJson().replaceFirst("\"employeeId\":\\s*\\d+,?\\s*", "");
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", LocalDateTime.now(), "PENDING", "IN_PREPARATION", null, "employee@mail.com");
        when(traceabilityHandler.registerStatusChange(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        verify(traceabilityHandler).registerStatusChange(any());
    }

    @Test
    void registerStatusChange_withMissingPreviousStatus_returns201AndInvokesHandler() throws Exception {
        // previousStatus is optional: an order's first status change (creation) has no previous status.
        String payload = validRequestJson().replaceFirst("\"previousStatus\":\\s*\"[^\"]*\",?\\s*", "");
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", LocalDateTime.now(), null, "PENDING", 7L, "employee@mail.com");
        when(traceabilityHandler.registerStatusChange(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        verify(traceabilityHandler).registerStatusChange(any());
    }

    @Test
    void registerStatusChange_withBlankNewStatus_returns400AndHandlerIsNeverCalled() throws Exception {
        String payload = validRequestJson().replace("\"IN_PREPARATION\"", "\"\"");

        mockMvc.perform(post("/api/v1/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traceabilityHandler);
    }

    @Test
    void getHistoryByCustomer_returnsOkWithTheAuthenticatedCustomerHistory() throws Exception {
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityHandler.getHistoryByCustomer(null)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/traceability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(123))
                .andExpect(jsonPath("$[0].customerId").value(15));
    }

    @Test
    void getHistoryByCustomer_withOrderId_passesOrderIdToHandler() throws Exception {
        TraceabilityResponseDto responseDto = new TraceabilityResponseDto("1", 123L, 15L,
                "customer@mail.com", LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityHandler.getHistoryByCustomer(123L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/traceability").param("orderId", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(123));

        verify(traceabilityHandler).getHistoryByCustomer(123L);
    }

    @Test
    void getHistoryByCustomer_whenNoHistoryExists_returnsOkWithEmptyList() throws Exception {
        when(traceabilityHandler.getHistoryByCustomer(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/traceability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
