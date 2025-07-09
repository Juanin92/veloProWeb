package com.veloproweb.service.reporting;

import com.veloproweb.model.dto.report.*;
import com.veloproweb.repository.reporting.ReportRepo;
import com.veloproweb.service.reporting.interfaces.IReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService implements IReportService {

    private final ReportRepo reportRepo;

    /**
     * Obtiene el conteo diario de ventas en un rango de fechas.
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else {
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findSalesByDateRange(startDate, endDate);
            List<DailySaleCountDTO> dtoList = new ArrayList<>();
            for (Object[] result : results){
                Date date = (Date) result[0];
                Long count = (Long) result[1];
                dtoList.add(new DailySaleCountDTO(date.toLocalDate(), count));
            }
            return dtoList;
        }
    }

    /**
     * Obtiene la suma diaria de ventas en un rango de fechas.
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else {
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findTotalSalesByDateRange(startDate, endDate);
            List<DailySaleSumDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal sum = (BigDecimal) result[1];
                dtoList.add(new DailySaleSumDTO(date.toLocalDate(), sum));
            }
            return dtoList;
        }
    }

    /**
     * Obtiene el promedio diario de ventas en un rango de fechas.
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else {
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findAverageSalesPerDay(startDate, endDate);
            List<DailySaleAvgDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal avg = (BigDecimal) result[1];
                dtoList.add(new DailySaleAvgDTO(date.toLocalDate(), avg));
            }
            return dtoList;
        }
    }

    /**
     * Obtiene la ganancia diaria de ventas en un rango de fechas.
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else {
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findEarningPerDay(startDate, endDate);
            List<DailySaleEarningDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal profit = (BigDecimal) result[1];
                dtoList.add(new DailySaleEarningDTO(date.toLocalDate(), profit));
            }
            return dtoList;
        }
    }

    /**
     * Obtiene lista de los productos más vendidos
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<ProductReportDTO> getMostProductSale(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findMostProductSale(startDate, endDate);
            List<ProductReportDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String brand = (String) result[1];
                String description = (String) result[2];
                BigDecimal total = (BigDecimal) result[3];
                dtoList.add(new ProductReportDTO(id, brand, description, null, total));
            }
            return dtoList;
        }
    }

    /**
     * Obtiene lista de las categorías más vendidas
     * @param start - Fecha de inicio del rango (inclusive).
     * @param end - Fecha de fin del rango (inclusive).
     * @return - Lista de objetos dto
     * @throws IllegalArgumentException Si la fecha de inicio es posterior a la fecha de fin.
     */
    @Override
    public List<ProductReportDTO> getMostCategorySale(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }else{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findMostCategorySale(startDate, endDate);
            List<ProductReportDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String categoryName = (String) result[1];
                BigDecimal total = (BigDecimal) result[2];
                dtoList.add(new ProductReportDTO(id, null, null, categoryName, total));
            }
            return dtoList;
        }
    }
}
