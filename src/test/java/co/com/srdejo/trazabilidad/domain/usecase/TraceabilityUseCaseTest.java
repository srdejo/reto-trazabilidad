package co.com.srdejo.trazabilidad.domain.usecase;

import co.com.srdejo.trazabilidad.domain.exception.OrderNotDeliveredException;
import co.com.srdejo.trazabilidad.domain.model.EmployeeRankingModel;
import co.com.srdejo.trazabilidad.domain.model.OrderDurationModel;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;
import co.com.srdejo.trazabilidad.infrastructure.exception.NoDataFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceabilityUseCaseTest {

    @Mock
    private ITraceabilityPersistencePort traceabilityPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    private TraceabilityUseCase traceabilityUseCase;

    @BeforeEach
    void setUp() {
        traceabilityUseCase = new TraceabilityUseCase(traceabilityPersistencePort, authenticatedUserPort);
    }

    private TraceabilityModel traceabilityModel() {
        return new TraceabilityModel(null, 123L, 15L, "customer@mail.com", null,
                "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
    }

    @Test
    void registerStatusChange_stampsCurrentDateAndDelegatesToPersistencePort() {
        TraceabilityModel model = traceabilityModel();
        TraceabilityModel saved = traceabilityModel();
        saved.setDate(LocalDateTime.now());
        when(traceabilityPersistencePort.save(model)).thenReturn(saved);

        LocalDateTime before = LocalDateTime.now();
        TraceabilityModel result = traceabilityUseCase.registerStatusChange(model);
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<TraceabilityModel> captor = ArgumentCaptor.forClass(TraceabilityModel.class);
        verify(traceabilityPersistencePort).save(captor.capture());
        assertThat(captor.getValue().getDate()).isBetween(before, after);
        assertThat(result).isEqualTo(saved);
    }

    @Test
    void getHistoryByCustomer_withoutOrderId_queriesPersistencePortWithTheAuthenticatedUserId() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(15L);
        List<TraceabilityModel> history = List.of(traceabilityModel());
        when(traceabilityPersistencePort.findByCustomerId(15L)).thenReturn(history);

        List<TraceabilityModel> result = traceabilityUseCase.getHistoryByCustomer(null);

        assertThat(result).isEqualTo(history);
        verify(traceabilityPersistencePort).findByCustomerId(15L);
    }

    @Test
    void getHistoryByCustomer_withOrderId_scopesQueryToAuthenticatedUserAndOrder() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(15L);
        List<TraceabilityModel> history = List.of(traceabilityModel());
        when(traceabilityPersistencePort.findByCustomerIdAndOrderId(15L, 123L)).thenReturn(history);

        List<TraceabilityModel> result = traceabilityUseCase.getHistoryByCustomer(123L);

        assertThat(result).isEqualTo(history);
        verify(traceabilityPersistencePort).findByCustomerIdAndOrderId(15L, 123L);
        verify(traceabilityPersistencePort, never()).findByCustomerId(org.mockito.ArgumentMatchers.any());
    }

    private TraceabilityModel recordMock(Long orderId, String previousStatus, String newStatus,
                                         LocalDateTime date, Long employeeId, String employeeEmail) {
        return new TraceabilityModel(null, orderId, 15L, "customer@mail.com", date,
                previousStatus, newStatus, employeeId, employeeEmail);
    }

    @Test
    void getOrderDuration_deliveredOrder_computesSecondsBetweenCreationAndDelivery() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        LocalDateTime end = start.plusMinutes(30);
        List<TraceabilityModel> records = List.of(
                recordMock(123L, null, "PENDING", start, null, ""),
                recordMock(123L, "PENDING", "IN_PREPARATION", start.plusMinutes(5), 7L, "employee@mail.com"),
                recordMock(123L, "IN_PREPARATION", "READY", start.plusMinutes(20), 7L, "employee@mail.com"),
                recordMock(123L, "READY", "DELIVERED", end, 7L, "employee@mail.com")
        );
        when(traceabilityPersistencePort.findByOrderInitAndFinishStatus(123L)).thenReturn(records);

        OrderDurationModel result = traceabilityUseCase.getOrderDuration(123L);

        assertThat(result.getOrderId()).isEqualTo(123L);
        assertThat(result.getStartDate()).isEqualTo(start);
        assertThat(result.getEndDate()).isEqualTo(end);
        assertThat(result.getDurationSeconds()).isEqualTo(1800L);
    }

    @Test
    void getOrderDuration_orderWithNoRecords_throwsNoDataFoundException() {
        when(traceabilityPersistencePort.findByOrderInitAndFinishStatus(999L)).thenReturn(List.of());

        assertThatThrownBy(() -> traceabilityUseCase.getOrderDuration(999L))
                .isInstanceOf(NoDataFoundException.class);
    }

    @Test
    void getOrderDuration_orderNotYetDelivered_throwsOrderNotDeliveredException() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        List<TraceabilityModel> records = List.of(
                recordMock(123L, null, "PENDING", start, null, ""),
                recordMock(123L, "PENDING", "IN_PREPARATION", start.plusMinutes(5), 7L, "employee@mail.com")
        );
        when(traceabilityPersistencePort.findByOrderInitAndFinishStatus(123L)).thenReturn(records);

        assertThatThrownBy(() -> traceabilityUseCase.getOrderDuration(123L))
                .isInstanceOf(OrderNotDeliveredException.class);
    }

    @Test
    void getEmployeeRanking_multipleEmployees_returnsAverageSortedAscending() {
        LocalDateTime start1 = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 1, 1, 11, 0, 0);
        LocalDateTime start3 = LocalDateTime.of(2026, 1, 1, 12, 0, 0);

        List<TraceabilityModel> deliveredRecords = List.of(
                recordMock(1L, "READY", "DELIVERED", start1.plusMinutes(10), 7L, "fast@mail.com"),
                recordMock(2L, "READY", "DELIVERED", start2.plusMinutes(30), 7L, "fast@mail.com"),
                recordMock(3L, "READY", "DELIVERED", start3.plusMinutes(50), 8L, "slow@mail.com")
        );
        List<TraceabilityModel> startRecords = List.of(
                recordMock(1L, null, "PENDING", start1, null, ""),
                recordMock(2L, null, "PENDING", start2, null, ""),
                recordMock(3L, null, "PENDING", start3, null, "")
        );
        when(traceabilityPersistencePort.findByNewStatus("DELIVERED")).thenReturn(deliveredRecords);
        when(traceabilityPersistencePort.findByOrderIdInAndPreviousStatusIsNull(List.of(1L, 2L, 3L)))
                .thenReturn(startRecords);

        List<EmployeeRankingModel> ranking = traceabilityUseCase.getEmployeeRanking();

        assertThat(ranking).hasSize(2);
        assertThat(ranking.getFirst().getEmployeeId()).isEqualTo(7L);
        assertThat(ranking.get(0).getDeliveredOrders()).isEqualTo(2);
        assertThat(ranking.get(0).getAverageDurationSeconds()).isEqualTo(1200.0); // avg(600, 1800)
        assertThat(ranking.get(1).getEmployeeId()).isEqualTo(8L);
        assertThat(ranking.get(1).getDeliveredOrders()).isEqualTo(1);
        assertThat(ranking.get(1).getAverageDurationSeconds()).isEqualTo(3000.0);
    }

    @Test
    void getEmployeeRanking_noDeliveredOrders_returnsEmptyList() {
        when(traceabilityPersistencePort.findByNewStatus("DELIVERED")).thenReturn(List.of());
        when(traceabilityPersistencePort.findByOrderIdInAndPreviousStatusIsNull(List.of())).thenReturn(List.of());

        List<EmployeeRankingModel> ranking = traceabilityUseCase.getEmployeeRanking();

        assertThat(ranking).isEmpty();
    }

    @Test
    void getEmployeeRanking_deliveredRecordWithoutEmployeeId_isExcluded() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        List<TraceabilityModel> deliveredRecords = List.of(
                recordMock(1L, "READY", "DELIVERED", start.plusMinutes(10), null, "")
        );
        List<TraceabilityModel> startRecords = List.of(
                recordMock(1L, null, "PENDING", start, null, "")
        );
        when(traceabilityPersistencePort.findByNewStatus("DELIVERED")).thenReturn(deliveredRecords);
        when(traceabilityPersistencePort.findByOrderIdInAndPreviousStatusIsNull(List.of(1L)))
                .thenReturn(startRecords);

        List<EmployeeRankingModel> ranking = traceabilityUseCase.getEmployeeRanking();

        assertThat(ranking).isEmpty();
    }
}
