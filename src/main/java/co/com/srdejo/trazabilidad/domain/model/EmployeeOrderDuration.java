package co.com.srdejo.trazabilidad.domain.model;

public record EmployeeOrderDuration(
        Long employeeId, String employeeEmail, long durationSeconds
) {
}
