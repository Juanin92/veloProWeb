package com.veloproweb.service.data;

import com.veloproweb.exceptions.data.LocalDataNotFoundException;
import com.veloproweb.model.dto.data.LocalDataDTO;
import com.veloproweb.model.entity.data.LocalData;
import com.veloproweb.repository.LocalDataRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalDataServiceTest {

    @InjectMocks private LocalDataService localDataService;
    @Mock private LocalDataRepo localDataRepo;

    //Prueba para actualizar los datos
    @Test
    public void updateData(){
        LocalDataDTO dto = LocalDataDTO.builder().name("newName").phone("+569 12345678").address("newAddress")
                .email("test@test.com").emailSecurityApp(null).build();
        LocalData data = LocalData.builder().id(1L).name("TestName").phone("+569 12345678").email("test@test.com")
                .address("TestAddress").emailSecurityApp("TestCode").build();
        when(localDataRepo.findById(1L)).thenReturn(Optional.of(data));

        ArgumentCaptor<LocalData> dataCaptor = ArgumentCaptor.forClass(LocalData.class);
        localDataService.updateData(dto);

        verify(localDataRepo).save(dataCaptor.capture());

        LocalData resultData = dataCaptor.getValue();
        assertEquals(dto.getName(), resultData.getName());
        assertEquals(dto.getAddress(), resultData.getAddress());
        assertEquals(dto.getAddress(), resultData.getAddress());
        assertEquals(data.getEmailSecurityApp(), resultData.getEmailSecurityApp());
    }
    @Test
    public void updateData_emailSecurityAppChange(){
        LocalDataDTO dto = LocalDataDTO.builder().name("newName").phone("+569 12345678").address("newAddress")
                .email("test@test.com").emailSecurityApp("TestCodeApp").build();
        LocalData data = LocalData.builder().id(1L).name("TestName").phone("+569 12345678").email("test@test.com")
                .address("TestAddress").emailSecurityApp("TestCode").build();
        when(localDataRepo.findById(1L)).thenReturn(Optional.of(data));

        ArgumentCaptor<LocalData> dataCaptor = ArgumentCaptor.forClass(LocalData.class);
        localDataService.updateData(dto);

        verify(localDataRepo).save(dataCaptor.capture());

        LocalData resultData = dataCaptor.getValue();
        assertEquals(dto.getName(), resultData.getName());
        assertEquals(dto.getAddress(), resultData.getAddress());
        assertEquals(dto.getAddress(), resultData.getAddress());
        assertEquals(dto.getEmailSecurityApp(), resultData.getEmailSecurityApp());
    }
    @Test
    public void updateData_DataNotFound(){
        LocalDataDTO dto = LocalDataDTO.builder().name("newName").phone("+569 12345678").address("newAddress")
                .email("test@test.com").emailSecurityApp(null).build();
        doThrow(new LocalDataNotFoundException("No se encontró la data")).when(localDataRepo).findById(1L);

        LocalDataNotFoundException e = assertThrows(LocalDataNotFoundException.class,
                () -> localDataService.updateData(dto));

        verify(localDataRepo, times(1)).findById(1L);
        verify(localDataRepo, never()).save(any(LocalData.class));

        assertEquals("No se encontró la data",e.getMessage());
    }

    //Prueba para obtener datos
    @Test
    public void getData(){
        LocalData data = LocalData.builder().id(1L).name("TestName").address("TestAddress").phone("+569 12345678")
                .email("test@test.com").emailSecurityApp("testCode").build();
        when(localDataRepo.findById(1L)).thenReturn(Optional.of(data));

        LocalDataDTO result = localDataService.getData();

        verify(localDataRepo, times(1)).findById(1L);

        assertEquals(data.getName(), result.getName());
    }
    @Test
    public void getData_emptyValue(){
        when(localDataRepo.findById(1L)).thenReturn(Optional.empty());

        LocalDataDTO result = localDataService.getData();

        verify(localDataRepo, times(1)).findById(1L);

        assertEquals("Nombre de la empresa", result.getName());
        assertEquals("+569 12345678", result.getPhone());
        assertEquals("Dirección", result.getAddress());
        assertEquals("email@example.com", result.getEmail());
        assertNull( result.getEmailSecurityApp());
    }

    //Prueba para obtener datos para enviar por email
    @Test
    public void getDataToEmail(){
        LocalData data = LocalData.builder().id(1L).name("TestName").address("TestAddress").phone("+569 12345678")
                .email("test@test.com").emailSecurityApp("testCode").build();
        when(localDataRepo.findById(1L)).thenReturn(Optional.of(data));

        LocalData result = localDataService.getDataToEmail();
        verify(localDataRepo, times(1)).findById(1L);

        assertEquals(data.getName(), result.getName());
    }
    @Test
    public void getDataToEmail_DataNotFound(){
        when(localDataRepo.findById(1L)).thenReturn(Optional.empty());

        LocalDataNotFoundException e = assertThrows(LocalDataNotFoundException.class,
                () -> localDataService.getDataToEmail());

        verify(localDataRepo, times(1)).findById(1L);

        assertEquals("No se encontró la data",e.getMessage());
    }
}
