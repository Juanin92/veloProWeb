package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.Report.DailySaleAvgDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleCountDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleEarningDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleSumDTO;
import com.veloProWeb.Repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService implements IReportService{

    @Autowired private ReportRepo reportRepo;

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

    @Override
    public List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findTotalSalesByDateRange(endDate, startDate);
            List<DailySaleSumDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal sum = (BigDecimal) result[1];
                dtoList.add(new DailySaleSumDTO(date.toLocalDate(), sum));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findAverageSalesPerDay(endDate, startDate);
            List<DailySaleAvgDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal avg = (BigDecimal) result[1];
                dtoList.add(new DailySaleAvgDTO(date.toLocalDate(), avg));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }

    @Override
    public List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end) {
        try{
            Date startDate = Date.valueOf(start);
            Date endDate = Date.valueOf(end);
            List<Object[]> results = reportRepo.findEarningPerDay(endDate, startDate);
            List<DailySaleEarningDTO> dtoList = new ArrayList<>();
            for (Object[] result : results) {
                Date date = (Date) result[0];
                BigDecimal profit = (BigDecimal) result[1];
                dtoList.add(new DailySaleEarningDTO(date.toLocalDate(), profit));
            }
            return dtoList;
        }catch (Exception e){
            throw new IllegalArgumentException("Debe ingresar fechas válidas en los campos");
        }
    }
}
