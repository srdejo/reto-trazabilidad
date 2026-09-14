package co.com.srdejo.trazabilidad.infrastructure.out.mongo.adapter;

import co.com.srdejo.trazabilidad.domain.model.ObjectModel;
import co.com.srdejo.trazabilidad.domain.spi.IObjectPersistencePort;
import co.com.srdejo.trazabilidad.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.entity.ObjectEntity;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.mapper.IObjectEntityMapper;
import co.com.srdejo.trazabilidad.infrastructure.out.mongo.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObjectMongoAdapter implements IObjectPersistencePort {

    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;


    @Override
    public ObjectModel saveObject(ObjectModel objectModel) {
        ObjectEntity objectEntity = objectRepository.save(objectEntityMapper.toEntity(objectModel));
        return objectEntityMapper.toObjectModel(objectEntity);
    }

    @Override
    public List<ObjectModel> getAllObjects() {
        List<ObjectEntity> entityList = objectRepository.findAll();
        if (entityList.isEmpty()) {
            throw new NoDataFoundException();
        }
        return objectEntityMapper.toObjectModelList(entityList);
    }
}
