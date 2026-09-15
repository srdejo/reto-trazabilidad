package co.com.srdejo.trazabilidad.application.dto.response;

import java.time.LocalDateTime;

public record TraceabilityResponseDto(
        String id,
        Long orderId,
        Long customerId,
        String customerEmail,
        LocalDateTime date,
        String previousStatus,
        String newStatus,
        Long employeeId,
        String employeeEmail
) {
}
