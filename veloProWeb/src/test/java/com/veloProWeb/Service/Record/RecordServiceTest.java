package com.veloProWeb.Service.Record;

import com.veloProWeb.Model.Entity.Record;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.RecordRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @InjectMocks private RecordService recordService;
    @Mock private RecordRepo recordRepo;
    private Record record;
    private User user;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);

        record = new Record();
        record.setId(1L);
        record.setEntryDate(LocalDateTime.now());
        record.setEndaDate(LocalDateTime.now());
        record.setActionDate(LocalDateTime.now());
        record.setAction("MODIFY");
        record.setComment("Prueba");
        record.setUser(user);
    }

    //Prueba para registrar una entrada del sistema
    @Test
    public void registerEntry_valid(){
        recordService.registerEntry(user);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepo).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertNotNull(savedRecord);
        assertEquals(user, savedRecord.getUser());
        assertNotNull(savedRecord.getEntryDate());
        assertNull(savedRecord.getEndaDate());
        assertNull(savedRecord.getActionDate());
        assertEquals("LOGIN", savedRecord.getAction());
        assertNull(savedRecord.getComment());
    }

    //Prueba para registrar una salida del sistema
    @Test
    public void registerEnd_valid() {
        recordService.registerEnd(user);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepo).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertNotNull(savedRecord);
        assertEquals(user, savedRecord.getUser());
        assertNull(savedRecord.getEntryDate());
        assertNotNull(savedRecord.getEndaDate());
        assertNull(savedRecord.getActionDate());
        assertEquals("LOGOUT", savedRecord.getAction());
        assertNull(savedRecord.getComment());
    }

    //Prueba para registrar una acción en el sistema
    @Test
    public void registerAction_valid() {
        String action = "MODIFY";
        String comment = "Actualización de datos";
        recordService.registerAction(user, action, comment);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepo).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertNotNull(savedRecord);
        assertEquals(user, savedRecord.getUser());
        assertNull(savedRecord.getEntryDate());
        assertNull(savedRecord.getEndaDate());
        assertNotNull(savedRecord.getActionDate());
        assertEquals(action, savedRecord.getAction());
        assertEquals(comment, savedRecord.getComment());
    }

    //Prueba para obtener una lista de registros
    @Test
    public void getAllRecord_valid() {
        List<Record> records = Collections.singletonList(record);
        when(recordRepo.findAll()).thenReturn(records);

        List<Record> result = recordService.getAllRecord();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(record, result.get(0));
        verify(recordRepo).findAll();
    }
}
