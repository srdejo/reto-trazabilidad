package co.com.srdejo.trazabilidad.application.mapper;

import co.com.srdejo.trazabilidad.application.dto.response.EmployeeRankingResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.OrderDurationResponseDto;
import co.com.srdejo.trazabilidad.application.dto.response.TraceabilityResponseDto;
import co.com.srdejo.trazabilidad.domain.model.EmployeeRankingModel;
import co.com.srdejo.trazabilidad.domain.model.OrderDurationModel;
import co.com.srdejo.trazabilidad.domain.model.TraceabilityModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ITraceabilityResponseMapper {

    TraceabilityResponseDto toResponse(TraceabilityModel traceabilityModel);
    List<TraceabilityResponseDto> toResponseList(List<TraceabilityModel> traceabilityModelList);
    OrderDurationResponseDto toDurationResponse(OrderDurationModel orderDurationModel);
    List<EmployeeRankingResponseDto> toRankingResponseList(List<EmployeeRankingModel> employeeRankingModelList);
}
