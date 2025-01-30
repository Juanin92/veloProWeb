package com.veloProWeb.Service.User;

import com.veloProWeb.Model.Entity.LocalData;
import com.veloProWeb.Repository.LocalDataRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocalDataServiceTest {

    @InjectMocks private LocalDataService localDataService;
    @Mock private LocalDataRepo localDataRepo;
    private LocalData localData;

    @BeforeEach
    void setUp(){
        localData = new LocalData();
        localData.setId(null);
        localData.setName("Prueba");
        localData.setPhone("123456789");
        localData.setEmail("Example@gmail.com");
        localData.setEmailSecurityApp("Prueba code");
        localData.setAddress("Dirección 1234");
    }

    //Prueba para guardar información
    @Test
    public void saveData_valid(){
        localDataService.saveData(localData);
        verify(localDataRepo).save(localData);
        assertEquals("Prueba", localData.getName());
        assertNotEquals("Test", localData.getName());
    }

    //Prueba para actualizar los datos
    @Test
    public void updateData_valid(){
        localData.setName("Test");
        localDataService.updateData(localData);
        verify(localDataRepo).save(localData);
        assertEquals("Test", localData.getName());
    }

    //Prueba para obtener datos
    @Test
    public void getData_valid(){
        List<LocalData> list = Collections.singletonList(localData);
        when(localDataRepo.findAll()).thenReturn(list);

        List<LocalData> result = localDataService.getData();
        verify(localDataRepo).findAll();
        assertEquals(list, result);
    }
}
