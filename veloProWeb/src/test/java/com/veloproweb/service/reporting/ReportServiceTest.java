package com.veloproweb.service.reporting;

import com.veloproweb.model.dto.report.*;
import com.veloproweb.repository.reporting.ReportRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getDailySale_valid(LocalDate startDate){
        Object[] result1 = {Date.valueOf(startDate), 10L};
        Object[] result2 = {Date.valueOf(start.plusDays(1)), 15L};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findSalesByDateRange(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<DailySaleCountDTO> results = reportService.getDailySale(startDate, end);
        assertEquals(2, results.size());
        assertEquals(10L, results.get(0).getSale());
        assertEquals(startDate, results.get(0).getDate());
        assertEquals(15L, results.get(1).getSale());
        verify(reportRepo, times(1)).findSalesByDateRange(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getDailySale_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);

        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getDailySale(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }

    //Prueba para obtener una lista de DailySaleSumDTO
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getTotalSaleDaily_valid(LocalDate startDate){
        Object[] result1 = {Date.valueOf(startDate), BigDecimal.valueOf(10)};
        Object[] result2 = {Date.valueOf(start.plusDays(1)), BigDecimal.valueOf(15)};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findTotalSalesByDateRange(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<DailySaleSumDTO> results = reportService.getTotalSaleDaily(startDate, end);
        assertEquals(2, results.size());
        assertEquals(BigDecimal.valueOf(10), results.get(0).getSum());
        assertEquals(startDate, results.get(0).getDate());
        assertEquals(BigDecimal.valueOf(15), results.get(1).getSum());
        verify(reportRepo, times(1)).findTotalSalesByDateRange(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getTotalSaleDaily_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);

        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getTotalSaleDaily(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }

    //Prueba para obtener una lista de DailySaleAvgDTO
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getAverageTotalSaleDaily_valid(LocalDate startDate){
        Object[] result1 = {Date.valueOf(startDate), BigDecimal.valueOf(10)};
        Object[] result2 = {Date.valueOf(start.plusDays(1)), BigDecimal.valueOf(15)};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findAverageSalesPerDay(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<DailySaleAvgDTO> results = reportService.getAverageTotalSaleDaily(startDate, end);
        assertEquals(2, results.size());
        assertEquals(BigDecimal.valueOf(10), results.get(0).getAvg());
        assertEquals(startDate, results.get(0).getDate());
        assertEquals(BigDecimal.valueOf(15), results.get(1).getAvg());
        verify(reportRepo, times(1)).findAverageSalesPerDay(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getAverageTotalSaleDaily_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);

        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getAverageTotalSaleDaily(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }

    //Prueba para obtener una lista de DailySaleEarningDTO
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getEarningSale_valid(LocalDate startDate){
        Object[] result1 = {Date.valueOf(startDate), BigDecimal.valueOf(10)};
        Object[] result2 = {Date.valueOf(start.plusDays(1)), BigDecimal.valueOf(15)};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findEarningPerDay(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<DailySaleEarningDTO> results = reportService.getEarningSale(startDate, end);
        assertEquals(2, results.size());
        assertEquals(10, results.get(0).getProfit());
        assertEquals(startDate, results.get(0).getDate());
        assertEquals(15, results.get(1).getProfit());
        verify(reportRepo, times(1)).findEarningPerDay(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getEarningSale_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);

        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getEarningSale(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }

    //Prueba para obtener los productos más vendidos
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getMostProductSale_valid(LocalDate startDate){
        Object[] result1 = {1L, "Brand Test", "Test description 1", BigDecimal.valueOf(20)};
        Object[] result2 = {2L, "Brand Test 2", "Test description 2", BigDecimal.valueOf(5)};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findMostProductSale(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<ProductReportDTO> results = reportService.getMostProductSale(startDate, end);
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("Brand Test", results.get(0).getBrand());
        assertEquals("Test description 2", results.get(1).getDescription());
        assertEquals(5, results.get(1).getTotal());
        assertNull(results.get(0).getCategoryName());
        verify(reportRepo, times(1)).findMostProductSale(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getMostProductSale_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);
        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getMostProductSale(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }

    //Prueba para obtener las categorías más vendidas
    @ParameterizedTest
    @CsvSource({"2024-12-12","2024-11-12","2024-10-12","2024-07-12","2024-01-12","2022-11-12","2025-01-12"})
    void getMostCategorySale_valid(LocalDate startDate){
        Object[] result1 = {1L, "Category Test", BigDecimal.valueOf(20)};
        Object[] result2 = {2L, "Category Test 2", BigDecimal.valueOf(5)};
        List<Object[]> mockResults = Arrays.asList(result1, result2);
        when(reportRepo.findMostCategorySale(Date.valueOf(startDate), Date.valueOf(end))).thenReturn(mockResults);

        List<ProductReportDTO> results = reportService.getMostCategorySale(startDate, end);
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("Category Test 2", results.get(1).getCategoryName());
        assertEquals(5, results.get(1).getTotal());
        assertNull(results.get(0).getBrand());
        assertNull(results.get(0).getDescription());
        verify(reportRepo, times(1)).findMostCategorySale(Date.valueOf(startDate), Date.valueOf(end));
    }
    @Test
    void getMostCategorySale_invalidDates(){
        LocalDate invalidStart = LocalDate.of(2025, 1, 15);
        Exception exception = assertThrows(IllegalArgumentException.class,() -> reportService.getMostCategorySale(invalidStart, end));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verifyNoInteractions(reportRepo);
    }
}
