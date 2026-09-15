package co.com.srdejo.trazabilidad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDurationModel {
    private Long orderId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long durationSeconds;
}
