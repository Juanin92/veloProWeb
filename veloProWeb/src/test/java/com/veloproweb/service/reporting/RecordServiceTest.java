package com.veloproweb.service.reporting;

import com.veloproweb.model.dto.reporting.RecordResponseDTO;
import com.veloproweb.model.entity.reporting.Record;
import com.veloproweb.model.entity.User.User;
import com.veloproweb.model.Enum.Rol;
import com.veloproweb.repository.reporting.RecordRepo;
import com.veloproweb.service.user.interfaces.IUserService;
import com.veloproweb.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @InjectMocks private RecordService recordService;
    @Mock private RecordRepo recordRepo;
    @Mock private IUserService userService;
    @Mock private UserValidator validator;
    @Mock private UserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp(){
        user = User.builder().id(1L).name("John").surname("Doe").username("johnny").role(Rol.ADMIN).build();
    }

    //Prueba para registrar una entrada del sistema
    @Test
    public void registerEntry(){
        when(userDetails.getUsername()).thenReturn("johnny");
        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(userDetails).getAuthorities();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateUserExists(user);
        doNothing().when(validator).validateUserHasRole(user);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        recordService.registerEntry(userDetails);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateUserExists(user);
        verify(validator, times(1)).validateUserHasRole(user);
        verify(recordRepo, times(1)).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertEquals(user, savedRecord.getUser());
        assertNotNull(savedRecord.getEntryDate());
        assertNull(savedRecord.getEndDate());
        assertNull(savedRecord.getActionDate());
        assertEquals("LOGIN", savedRecord.getAction());
        assertNull(savedRecord.getComment());
    }

    //Prueba para registrar una salida del sistema
    @Test
    public void registerEnd() {
        when(userDetails.getUsername()).thenReturn("johnny");
        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(userDetails).getAuthorities();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateUserExists(user);
        doNothing().when(validator).validateUserHasRole(user);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        recordService.registerEnd(userDetails);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateUserExists(user);
        verify(validator, times(1)).validateUserHasRole(user);
        verify(recordRepo, times(1)).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertEquals(user, savedRecord.getUser());
        assertNull(savedRecord.getEntryDate());
        assertNotNull(savedRecord.getEndDate());
        assertNull(savedRecord.getActionDate());
        assertEquals("LOGOUT", savedRecord.getAction());
        assertNull(savedRecord.getComment());
    }

    //Prueba para registrar una acción en el sistema
    @Test
    public void registerAction() {
        when(userDetails.getUsername()).thenReturn("johnny");
        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(userDetails).getAuthorities();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateUserExists(user);
        doNothing().when(validator).validateUserHasRole(user);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        recordService.registerAction(userDetails, "MODIFY", "Actualización de datos");

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateUserExists(user);
        verify(validator, times(1)).validateUserHasRole(user);
        verify(recordRepo, times(1)).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertEquals(user, savedRecord.getUser());
        assertNull(savedRecord.getEntryDate());
        assertNull(savedRecord.getEndDate());
        assertNotNull(savedRecord.getActionDate());
        assertEquals("MODIFY", savedRecord.getAction());
        assertEquals("Actualización de datos", savedRecord.getComment());
    }

    //Prueba para obtener una lista de registros
    @Test
    public void getAllRecord() {
        Record loginRecord = Record.builder().id(1L).user(user).comment(null).action("LOGIN")
                .entryDate(LocalDateTime.now()).actionDate(null).endDate(null).build();
        Record logoutRecord = Record.builder().id(1L).user(user).comment(null).action("LOGOUT")
                .entryDate(null).actionDate(null).endDate(LocalDateTime.now()).build();
        List<Record> records = List.of(loginRecord, logoutRecord);
        when(recordRepo.findAll()).thenReturn(records);

        List<RecordResponseDTO> result = recordService.getAllRecord();

        verify(recordRepo, times(1)).findAll();

        assertNotNull(result);
        assertEquals(records.size(), result.size());
        assertEquals(records.getFirst().getAction(), result.getFirst().getAction());
        assertEquals(records.getLast().getAction(), result.getLast().getAction());
    }

    @Test
    void registerActionManual() {
        when(userService.getUserByUsername("johnny")).thenReturn(user);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        recordService.registerActionManual("johnny", "MODIFY", "Actualización de datos");

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(recordRepo, times(1)).save(recordCaptor.capture());

        Record savedRecord = recordCaptor.getValue();
        assertEquals(user, savedRecord.getUser());
        assertEquals("MODIFY", savedRecord.getAction());
        assertEquals("Actualización de datos", savedRecord.getComment());
    }
}
