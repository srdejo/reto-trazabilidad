package co.com.srdejo.trazabilidad.application.handler.impl;

import co.com.srdejo.trazabilidad.application.dto.request.ObjectRequestDto;
import co.com.srdejo.trazabilidad.application.dto.response.ObjectResponseDto;
import co.com.srdejo.trazabilidad.application.handler.IObjectHandler;
import co.com.srdejo.trazabilidad.application.mapper.IObjectRequestMapper;
import co.com.srdejo.trazabilidad.application.mapper.IObjectResponseMapper;
import co.com.srdejo.trazabilidad.domain.api.IObjectServicePort;
import co.com.srdejo.trazabilidad.domain.model.ObjectModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ObjectHandler implements IObjectHandler {

    private final IObjectServicePort objectServicePort;
    private final IObjectRequestMapper objectRequestMapper;
    private final IObjectResponseMapper objectResponseMapper;

    @Override
    public void saveObject(ObjectRequestDto objectRequestDto) {
        ObjectModel objectModel = objectRequestMapper.toObject(objectRequestDto);
        objectServicePort.saveObject(objectModel);
    }

    @Override
    public List<ObjectResponseDto> getAllObjects() {
        return objectResponseMapper.toResponseList(objectServicePort.getAllObjects());
    }
}