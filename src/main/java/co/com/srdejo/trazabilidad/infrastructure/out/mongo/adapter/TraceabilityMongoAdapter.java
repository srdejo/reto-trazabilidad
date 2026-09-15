package co.com.srdejo.trazabilidad.infrastructure.out.mongo.adapter;

import co.com.srdejo.trazabilidad.domain.model.OrderStatus;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.domain.spi.ITraceabilityPersistencePort;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper.ITraceabilityEntityMapper;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository.ITraceabilityRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TraceabilityMongoAdapter implements ITraceabilityPersistencePort {

    private final ITraceabilityRepository traceabilityRepository;
    private final ITraceabilityEntityMapper traceabilityEntityMapper;

    @Override
    public TraceabilityModel save(TraceabilityModel traceabilityModel) {
        var entity = traceabilityEntityMapper.toEntity(traceabilityModel);
        var saved = traceabilityRepository.save(entity);
        return traceabilityEntityMapper.toModel(saved);
    }

    @Override
    public List<TraceabilityModel> findByCustomerId(Long customerId) {
        return traceabilityRepository.findByCustomerIdOrderByDateAsc(customerId)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<TraceabilityModel> findByCustomerIdAndOrderId(Long customerId, Long orderId) {
        return traceabilityRepository.findByCustomerIdAndOrderIdOrderByDateAsc(customerId, orderId)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<TraceabilityModel> findByOrderId(Long orderId) {
        return traceabilityRepository.findByOrderId(orderId)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<TraceabilityModel> findByNewStatus(String newStatus) {
        return traceabilityRepository.findByNewStatus(newStatus)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<TraceabilityModel> findByOrderIdInAndPreviousStatusIsNull(List<Long> orderIds) {
        return traceabilityRepository.findByOrderIdInAndPreviousStatusIsNull(orderIds)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<TraceabilityModel> findByOrderInitAndFinishStatus(Long orderId) {
        return traceabilityRepository
                .findByOrderIdAndNewStatusOrOrderIdAndPreviousStatusIsNull(
                        orderId, OrderStatus.DELIVERED.name(), orderId)
                .stream()
                .map(traceabilityEntityMapper::toModel)
                .toList();
    }
}
