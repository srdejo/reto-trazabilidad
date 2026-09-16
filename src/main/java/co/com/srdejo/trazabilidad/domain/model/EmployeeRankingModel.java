package co.com.srdejo.trazabilidad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeRankingModel {
    private Long employeeId;
    private String employeeEmail;
    private long deliveredOrders;
    private double averageDurationSeconds;
}
