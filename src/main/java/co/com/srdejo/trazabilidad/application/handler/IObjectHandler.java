package co.com.srdejo.trazabilidad.application.handler;

import co.com.srdejo.trazabilidad.application.dto.request.ObjectRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.ObjectResponseDto;

import java.util.List;

public interface IObjectHandler {

    void saveObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();
}