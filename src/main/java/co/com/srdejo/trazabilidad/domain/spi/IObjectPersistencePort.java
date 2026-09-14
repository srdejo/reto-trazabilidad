package co.com.srdejo.trazabilidad.domain.spi;

import co.com.srdejo.trazabilidad.domain.model.ObjectModel;
import java.util.List;

public interface IObjectPersistencePort {
    ObjectModel saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
}