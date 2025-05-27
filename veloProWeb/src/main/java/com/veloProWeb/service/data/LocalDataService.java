package com.veloProWeb.service.data;

import com.veloProWeb.model.entity.data.LocalData;
import com.veloProWeb.repository.LocalDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalDataService implements ILocalDataService {

    @Autowired private LocalDataRepo localDataRepo;

    /**
     * Actualiza los datos locales
     * @param localData - Objeto con los datos para actualizar
     */
    @Override
    public void updateData(LocalData localData) {
        LocalData existingData = localDataRepo.findById(1L).orElseThrow(() ->new IllegalArgumentException("No se encontró la data"));
        existingData.setName(localData.getName());
        existingData.setPhone(localData.getPhone());
        existingData.setEmail(localData.getEmail());
        if (localData.getEmailSecurityApp() != null && !localData.getEmailSecurityApp().trim().isEmpty()) {
            existingData.setEmailSecurityApp(localData.getEmailSecurityApp());
        }else {
            existingData.setEmailSecurityApp(existingData.getEmailSecurityApp());
        }
        existingData.setAddress(localData.getAddress());
        localDataRepo.save(existingData);
    }

    /**
     * Obtener una lista con los datos locales
     * @return - Lista con los datos locales
     */
    @Override
    public List<LocalData> getData() {
        List<LocalData> dataList = localDataRepo.findAll();
        if (dataList.isEmpty()) {
            LocalData localData = createLocalData();
            dataList.add(localData);
        }else{
            dataList = dataList.stream()
                    .peek(localData -> localData.setEmailSecurityApp(null))
                    .collect(Collectors.toList());
        }
        return dataList;
    }

    /**
     * Obtener los datos locales
     * @return - Objeto con los datos necesarios
     */
    @Override
    public List<LocalData> getDataToEmail() {
        return localDataRepo.findAll();
    }

    private LocalData createLocalData(){
        LocalData localData = new LocalData();
        localData.setName("Nombre de la empresa");
        localData.setPhone("+569 12345678");
        localData.setEmail("email@example.com");
        localData.setEmailSecurityApp("Llave Acceso");
        localData.setAddress("Dirección");
        return localData;
    }
}
