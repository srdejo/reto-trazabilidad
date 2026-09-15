package co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper;

import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity.TraceabilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ITraceabilityEntityMapper {

    TraceabilityEntity toEntity(TraceabilityModel user);
    TraceabilityModel toModel(TraceabilityEntity traceabilityEntity);
}
