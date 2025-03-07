package com.veloProWeb.Service.Record;

import com.veloProWeb.Model.Entity.Record;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Model.Enum.Rol;
import com.veloProWeb.Repository.RecordRepo;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @InjectMocks private RecordService recordService;
    @Mock private RecordRepo recordRepo;
    @Mock private IUserService userService;
    private Record record;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setRole(Rol.ADMIN);

        record = new Record();
        record.setId(1L);
    }

    private UserDetails createUserDetailsWithRole(Rol role) {
        List<GrantedAuthority> authorities = Stream.of(role.name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User("testUser", "password", authorities);
    }

    //Prueba para registrar una entrada del sistema
    @Test
    public void registerEntry_valid(){
        userDetails = createUserDetailsWithRole(Rol.ADMIN);
        when(userService.getUserWithUsername("testUser")).thenReturn(user);
        recordService.registerEntry(userDetails);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(userService, times(1)).getUserWithUsername("testUser");
        verify(recordRepo, times(1)).save(recordCaptor.capture());

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
        userDetails = createUserDetailsWithRole(Rol.ADMIN);
        when(userService.getUserWithUsername("testUser")).thenReturn(user);
        recordService.registerEnd(userDetails);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(userService, times(1)).getUserWithUsername("testUser");
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
        userDetails = createUserDetailsWithRole(Rol.ADMIN);
        when(userService.getUserWithUsername("testUser")).thenReturn(user);
        String action = "MODIFY";
        String comment = "Actualización de datos";
        recordService.registerAction(userDetails, action, comment);
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(userService, times(1)).getUserWithUsername("testUser");
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
