package com.veloProWeb.Service.User;

import com.veloProWeb.Model.Entity.LocalData;
import com.veloProWeb.Repository.LocalDataRepo;
import com.veloProWeb.Service.User.Interface.ILocalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalDataService implements ILocalDataService {

    @Autowired private LocalDataRepo localDataRepo;

    /**
     * Crear información local
     * @param data - Objeto con la información local necesaria
     */
    @Override
    public void saveData(LocalData data) {
        LocalData localData =  new LocalData();
        localData.setId(null);
        localData.setName(data.getName());
        localData.setPhone(data.getPhone());
        localData.setEmail(data.getEmail());
        localData.setEmailSecurityApp(data.getEmailSecurityApp());
        localData.setAddress(data.getAddress());
        localDataRepo.save(localData);
    }

    /**
     * Actualiza los datos locales
     * @param localData - Objeto con los datos para actualizar
     */
    @Override
    public void updateData(LocalData localData) {
        localDataRepo.save(localData);
    }

    /**
     * Obtener una lista con los datos locales
     * @return - Lista con los datos locales
     */
    @Override
    public List<LocalData> getData() {
        return localDataRepo.findAll();
    }
}
