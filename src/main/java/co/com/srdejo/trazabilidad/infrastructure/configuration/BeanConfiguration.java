package co.com.srdejo.trazabilidad.infrastructure.configuration;

import co.com.srdejo.trazabilidad.domain.api.IObjectServicePort;
import co.com.srdejo.trazabilidad.domain.spi.IObjectPersistencePort;
import co.com.srdejo.trazabilidad.domain.usecase.ObjectUseCase;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.adapter.ObjectMongoAdapter;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper.IObjectEntityMapper;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;

    @Bean
    public IObjectPersistencePort objectPersistencePort() {
        return new ObjectMongoAdapter(objectRepository, objectEntityMapper);
    }

    @Bean
    public IObjectServicePort objectServicePort() {
        return new ObjectUseCase(objectPersistencePort());
    }
}