package co.com.srdejo.trazabilidad.domain.usecase;

import co.com.srdejo.trazabilidad.domain.api.ITraceabilityServicePort;
import co.com.srdejo.trazabilidad.domain.exception.OrderNotDeliveredException;
import co.com.srdejo.trazabilidad.domain.model.*;
import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;
import co.com.srdejo.trazabilidad.domain.utils.DomainConstants;
import co.com.srdejo.trazabilidad.infrastructure.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class TraceabilityUseCase implements ITraceabilityServicePort {

    private final ITraceabilityPersistencePort traceabilityPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public TraceabilityUseCase(ITraceabilityPersistencePort traceabilityPersistencePort,
                               IAuthenticatedUserPort authenticatedUserPort) {
        this.traceabilityPersistencePort = traceabilityPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public TraceabilityModel registerStatusChange(TraceabilityModel traceabilityModel) {
        traceabilityModel.setDate(LocalDateTime.now(ZoneId.of("America/Bogota")));
        TraceabilityModel saved = traceabilityPersistencePort.save(traceabilityModel);
        log.info("Traceability record registered for order {} ({} -> {})",
                saved.getOrderId(), saved.getPreviousStatus(), saved.getNewStatus());
        return saved;
    }

    @Override
    public List<TraceabilityModel> getHistoryByCustomer(Long orderId) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        if (orderId != null) {
            return traceabilityPersistencePort.findByCustomerIdAndOrderId(authenticatedUserId, orderId);
        }
        return traceabilityPersistencePort.findByCustomerId(authenticatedUserId);
    }

    @Override
    public OrderDurationModel getOrderDuration(Long orderId) {
        List<TraceabilityModel> records = traceabilityPersistencePort.findByOrderInitAndFinishStatus(orderId);
        if (records.isEmpty()) {
            log.warn("No traceability records found for order {}", orderId);
            throw new NoDataFoundException();
        }
        LocalDateTime startDate = findDate(records,
                orderTrace -> orderTrace.getPreviousStatus() == null,
                NoDataFoundException::new);
        LocalDateTime endDate = findDate(records,
                orderTrace -> OrderStatus.DELIVERED.name().equals(orderTrace.getNewStatus()),
                () -> {
                    log.warn("Order {} has not been delivered yet, duration cannot be computed", orderId);
                    return new OrderNotDeliveredException();
                });

        long durationSeconds = Duration.between(getInstant(startDate), getInstant(endDate)).getSeconds();
        log.debug("Order {} duration computed: {} seconds", orderId, durationSeconds);
        return new OrderDurationModel(orderId, startDate, endDate, durationSeconds);
    }

    @Override
    public List<EmployeeRankingModel> getEmployeeRanking() {
        List<TraceabilityModel> deliveredRecords = traceabilityPersistencePort.findByNewStatus(OrderStatus.DELIVERED.name());
        Map<Long, LocalDateTime> startDatesByOrderId = getStartDatesByOrderId(deliveredRecords);
        List<EmployeeOrderDuration> employeeOrderDurations = getEmployeeOrderDurations(deliveredRecords, startDatesByOrderId);
        List<EmployeeRankingModel> ranking = buildRanking(employeeOrderDurations);
        log.debug("Employee ranking computed for {} employees from {} delivered orders", ranking.size(), deliveredRecords.size());
        return ranking;
    }

    private Map<Long, LocalDateTime> getStartDatesByOrderId(List<TraceabilityModel> deliveredRecords) {
        List<Long> orderIds = deliveredRecords.stream()
                .map(TraceabilityModel::getOrderId)
                .distinct()
                .toList();

        return traceabilityPersistencePort
                .findByOrderIdInAndPreviousStatusIsNull(orderIds)
                .stream()
                .collect(Collectors.toMap(TraceabilityModel::getOrderId, TraceabilityModel::getDate));
    }

    private List<EmployeeOrderDuration> getEmployeeOrderDurations(List<TraceabilityModel> deliveredRecords,
                                                                    Map<Long, LocalDateTime> startDatesByOrderId) {
        return deliveredRecords.stream()
                .filter(orderTrace -> orderTrace.getEmployeeId() != null)
                .filter(orderTrace -> startDatesByOrderId.containsKey(orderTrace.getOrderId()))
                .map(orderTrace -> new EmployeeOrderDuration(
                        orderTrace.getEmployeeId(),
                        orderTrace.getEmployeeEmail(),
                        Duration.between(
                                getInstant(startDatesByOrderId.get(orderTrace.getOrderId())),
                                getInstant(orderTrace.getDate())
                        ).getSeconds()
                ))
                .toList();
    }

    private List<EmployeeRankingModel> buildRanking(List<EmployeeOrderDuration> employeeOrderDurations) {
        return employeeOrderDurations.stream()
                .collect(Collectors.groupingBy(EmployeeOrderDuration::employeeId))
                .entrySet().stream()
                .map(entry -> {
                    List<EmployeeOrderDuration> durations = entry.getValue();
                    double averageDurationSeconds = durations.stream()
                            .mapToLong(EmployeeOrderDuration::durationSeconds)
                            .average()
                            .orElse(0);
                    return new EmployeeRankingModel(
                            entry.getKey(),
                            durations.getFirst().employeeEmail(),
                            durations.size(),
                            averageDurationSeconds
                    );
                })
                .sorted(Comparator.comparingDouble(EmployeeRankingModel::getAverageDurationSeconds))
                .toList();
    }

    private LocalDateTime findDate(List<TraceabilityModel> records, Predicate<TraceabilityModel> predicate,
                                   Supplier<RuntimeException> exceptionSupplier) {
        return records.stream()
                .filter(predicate)
                .map(TraceabilityModel::getDate)
                .findFirst()
                .orElseThrow(exceptionSupplier);
    }

    private Instant getInstant(LocalDateTime dateTime) {
        ZoneId zone = ZoneId.of(DomainConstants.APPLICATION_ZONE_ID.getId());
        return dateTime.atZone(zone).toInstant();
    }
}