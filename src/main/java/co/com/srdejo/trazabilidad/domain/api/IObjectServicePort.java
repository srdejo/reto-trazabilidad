package co.com.srdejo.trazabilidad.domain.api;

import co.com.srdejo.trazabilidad.domain.model.ObjectModel;

import java.util.List;

public interface IObjectServicePort {

    void saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
}