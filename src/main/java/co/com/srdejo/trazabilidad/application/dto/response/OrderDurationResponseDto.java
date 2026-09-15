package co.com.srdejo.trazabilidad.application.dto.response;

import java.time.LocalDateTime;

public record OrderDurationResponseDto(
        Long orderId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        long durationSeconds
) {
}
