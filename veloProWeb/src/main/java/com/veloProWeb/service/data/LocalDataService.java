package com.veloProWeb.service.data;

import com.veloProWeb.exceptions.data.LocalDataNotFoundException;
import com.veloProWeb.model.dto.data.LocalDataDTO;
import com.veloProWeb.model.entity.data.LocalData;
import com.veloProWeb.repository.LocalDataRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class LocalDataService implements ILocalDataService {

    private final LocalDataRepo localDataRepo;
    private final static Long LOCAL_DATA_ID = 1L;

    /**
     * Actualiza los datos locales
     * @param localData - Objeto con los datos para actualizar
     */
    @Transactional
    @Override
    public void updateData(LocalDataDTO localData) {
        LocalData existingData = localDataRepo.findById(LOCAL_DATA_ID)
                .orElseThrow(() ->new LocalDataNotFoundException("No se encontró la data"));
        existingData.setName(localData.getName());
        existingData.setPhone(localData.getPhone());
        existingData.setEmail(localData.getEmail());
        updateEmailSecurityIfPresent(localData, existingData);
        existingData.setAddress(localData.getAddress());
        localDataRepo.save(existingData);
    }

    /**
     * Obtener una lista con los datos locales
     *
     * @return - Lista con los datos locales
     */
    @Override
    public LocalDataDTO getData() {
        LocalData data = localDataRepo.findById(LOCAL_DATA_ID).orElse(null);
        if (data == null) {
            LocalData newData = createLocalData();
            return mapToDTO(newData);
        }else{
            return mapToDTO(data);
        }
    }

    /**
     * Obtener los datos locales
     * @return - Objeto con los datos necesarios
     */
    @Override
    public LocalData getDataToEmail() {
        return localDataRepo.findById(LOCAL_DATA_ID)
                .orElseThrow(() ->new LocalDataNotFoundException("No se encontró la data"));
    }

    /**
     * Actualiza código de seguridad del email si está presente en el DTO
     * @param dto - DTO con los datos a actualizar
     * @param entity - Entidad LocalData a actualizar
     */
    private void updateEmailSecurityIfPresent(LocalDataDTO dto, LocalData entity) {
        if (dto.getEmailSecurityApp() != null && !dto.getEmailSecurityApp().trim().isEmpty()) {
            entity.setEmailSecurityApp(dto.getEmailSecurityApp());
        }
    }

    /**
     * Crea un objeto LocalData con datos de ejemplo
     * @return - Objeto LocalData
     */
    private LocalData createLocalData(){
        return LocalData.builder()
                .id(LOCAL_DATA_ID)
                .name("Nombre de la empresa")
                .phone("+569 12345678")
                .address("Dirección")
                .email("email@example.com")
                .emailSecurityApp("Llave Acceso")
                .build();
    }

    /**
     * Mapea un LocalData a LocalDataDTO
     * @param localData - Objeto a mapear
     * @return - DTO con los datos del LocalData
     */
    private LocalDataDTO mapToDTO(LocalData localData){
        return LocalDataDTO.builder()
                .name(localData.getName())
                .phone(localData.getPhone())
                .address(localData.getAddress())
                .email(localData.getEmail())
                .emailSecurityApp(null)
                .build();
    }
}
