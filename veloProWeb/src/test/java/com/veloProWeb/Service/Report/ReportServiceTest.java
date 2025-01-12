package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.Report.DailySaleCountDTO;
import com.veloProWeb.Repository.ReportRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks private ReportService reportService;
    @Mock private ReportRepo reportRepo;
    private LocalDate start;
    private LocalDate end;

    @BeforeEach
    void setUp(){
        start = LocalDate.of(2024,12,12);
        end = LocalDate.of(2025, 1, 12);
    }

    //Prueba para obtener una lista de DailySaleCountDTO
    @Test
    public void getDailySale_valid(){
        Object[] result1 = {Date.valueOf(start), 10L};
        Object[] result2 = {Date.valueOf(start.plusDays(1)), 15L};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findSalesByDateRange(Date.valueOf(start), Date.valueOf(end))).thenReturn(mockResults);

        List<DailySaleCountDTO> results = reportService.getDailySale(start, end);
        assertEquals(2, results.size());
        assertEquals(10L, results.get(0).getSale());
        assertEquals(start, results.get(0).getDate());
        assertEquals(15L, results.get(1).getSale());
        verify(reportRepo, times(1)).findSalesByDateRange(Date.valueOf(start), Date.valueOf(end));
    }
    @Test
    public void getDailySale_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);

        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getDailySale(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }
}
