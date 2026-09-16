package co.com.srdejo.trazabilidad.application.dto.response;

public record EmployeeRankingResponseDto(
        Long employeeId,
        String employeeEmail,
        long deliveredOrders,
        double averageDurationSeconds
) {
}
