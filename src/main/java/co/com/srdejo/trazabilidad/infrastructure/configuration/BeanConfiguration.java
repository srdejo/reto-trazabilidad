package co.com.srdejo.trazabilidad.infrastructure.configuration;

import co.com.srdejo.trazabilidad.domain.api.ITraceabilityServicePort;
import co.com.srdejo.trazabilidad.domain.spi.IAuthenticatedUserPort;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;
import co.com.srdejo.trazabilidad.domain.usecase.TraceabilityUseCase;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.adapter.TraceabilityMongoAdapter;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper.ITraceabilityEntityMapper;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository.ITraceabilityRepository;
import co.com.srdejo.trazabilidad.infrastructure.out.security.SecurityContextUserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final ITraceabilityRepository traceabilityRepository;
    private final ITraceabilityEntityMapper traceabilityEntityMapper;

    @Bean
    public IAuthenticatedUserPort authenticatedUserPort() {
        return new SecurityContextUserAdapter();
    }

    @Bean
    public ITraceabilityPersistencePort traceabilityPersistencePort() {
        return new TraceabilityMongoAdapter(traceabilityRepository, traceabilityEntityMapper);
    }

    @Bean
    public ITraceabilityServicePort traceabilityServicePort() {
        return new TraceabilityUseCase(traceabilityPersistencePort(), authenticatedUserPort());
    }
}