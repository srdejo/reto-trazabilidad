package co.com.srdejo.trazabilidad.domain.usecase;

import co.com.srdejo.trazabilidad.domain.api.ITraceabilityServicePort;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
        return traceabilityPersistencePort.save(traceabilityModel);
    }

    @Override
    public List<TraceabilityModel> getHistoryByCustomer(Long orderId) {
        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        if (orderId != null) {
            return traceabilityPersistencePort.findByCustomerIdAndOrderId(authenticatedUserId, orderId);
        }
        return traceabilityPersistencePort.findByCustomerId(authenticatedUserId);
    }
}