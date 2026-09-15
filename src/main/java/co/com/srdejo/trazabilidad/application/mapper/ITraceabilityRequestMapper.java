package co.com.srdejo.trazabilidad.application.mapper;

import co.com.srdejo.trazabilidad.application.dto.request.TraceabilityRequestDto;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ITraceabilityRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    TraceabilityModel toModel(TraceabilityRequestDto traceabilityRequestDto);

}
