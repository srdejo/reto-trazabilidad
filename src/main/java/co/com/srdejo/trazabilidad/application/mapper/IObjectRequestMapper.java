package co.com.srdejo.trazabilidad.application.mapper;

import co.com.srdejo.trazabilidad.application.dto.request.ObjectRequestDto;
import co.com.srdejo.trazabilidad.domain.model.ObjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IObjectRequestMapper {
    ObjectModel toObject(ObjectRequestDto objectRequestDto);
}
