package co.com.srdejo.trazabilidad.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraceabilityRequestDto(
        @NotNull Long orderId,
        @NotNull Long customerId,
        @NotBlank String customerEmail,
        String previousStatus,
        @NotBlank String newStatus,
        Long employeeId,
        String employeeEmail
) { }