package co.com.srdejo.trazabilidad.application.mapper;

import co.com.srdejo.trazabilidad.application.dto.response.ObjectResponseDto;
import co.com.srdejo.trazabilidad.domain.model.ObjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IObjectResponseMapper {
    ObjectResponseDto toResponse(ObjectModel objectModel);

    List<ObjectResponseDto> toResponseList(List<ObjectModel> objectModelList);
}
