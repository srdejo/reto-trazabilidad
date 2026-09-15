package co.com.srdejo.trazabilidad.infrastructure.out.mongo.adapter;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity.TraceabilityEntity;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper.ITraceabilityEntityMapper;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository.ITraceabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityMongoAdapterTest {

    @Mock
    private ITraceabilityRepository traceabilityRepository;

    @Mock
    private ITraceabilityEntityMapper traceabilityEntityMapper;

    private TraceabilityMongoAdapter traceabilityMongoAdapter;

    @BeforeEach
    void setUp() {
        traceabilityMongoAdapter = new TraceabilityMongoAdapter(traceabilityRepository, traceabilityEntityMapper);
    }

    @Test
    void save_mapsModelToEntitySavesAndMapsBack() {
        TraceabilityModel model = new TraceabilityModel(null, 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        TraceabilityEntity entityToSave = new TraceabilityEntity();
        TraceabilityEntity savedEntity = new TraceabilityEntity();
        TraceabilityModel expectedModel = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                model.getDate(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityEntityMapper.toEntity(model)).thenReturn(entityToSave);
        when(traceabilityRepository.save(entityToSave)).thenReturn(savedEntity);
        when(traceabilityEntityMapper.toModel(savedEntity)).thenReturn(expectedModel);

        TraceabilityModel result = traceabilityMongoAdapter.save(model);

        assertThat(result).isEqualTo(expectedModel);
    }

    @Test
    void findByCustomerId_mapsEachRepositoryEntityToAModel() {
        TraceabilityEntity entity = new TraceabilityEntity();
        TraceabilityModel model = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityRepository.findByCustomerIdOrderByDateAsc(15L)).thenReturn(List.of(entity));
        when(traceabilityEntityMapper.toModel(entity)).thenReturn(model);

        List<TraceabilityModel> result = traceabilityMongoAdapter.findByCustomerId(15L);

        assertThat(result).containsExactly(model);
    }

    @Test
    void findByCustomerId_whenRepositoryHasNoRecords_returnsEmptyList() {
        when(traceabilityRepository.findByCustomerIdOrderByDateAsc(99L)).thenReturn(List.of());

        List<TraceabilityModel> result = traceabilityMongoAdapter.findByCustomerId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByCustomerIdAndOrderId_mapsEachRepositoryEntityToAModel() {
        TraceabilityEntity entity = new TraceabilityEntity();
        TraceabilityModel model = new TraceabilityModel("1", 123L, 15L, "customer@mail.com",
                LocalDateTime.now(), "PENDING", "IN_PREPARATION", 7L, "employee@mail.com");
        when(traceabilityRepository.findByCustomerIdAndOrderIdOrderByDateAsc(15L, 123L)).thenReturn(List.of(entity));
        when(traceabilityEntityMapper.toModel(entity)).thenReturn(model);

        List<TraceabilityModel> result = traceabilityMongoAdapter.findByCustomerIdAndOrderId(15L, 123L);

        assertThat(result).containsExactly(model);
    }

    @Test
    void findByCustomerIdAndOrderId_whenOrderBelongsToAnotherCustomer_returnsEmptyList() {
        when(traceabilityRepository.findByCustomerIdAndOrderIdOrderByDateAsc(15L, 999L)).thenReturn(List.of());

        List<TraceabilityModel> result = traceabilityMongoAdapter.findByCustomerIdAndOrderId(15L, 999L);

        assertThat(result).isEmpty();
    }
}
