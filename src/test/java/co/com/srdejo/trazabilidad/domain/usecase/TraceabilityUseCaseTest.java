package co.com.srdejo.trazabilidad.domain.usecase;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}
