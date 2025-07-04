package com.veloproweb.service.reporting.interfaces;

import com.veloproweb.model.dto.report.*;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {
    List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end);
    List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end);
    List<ProductReportDTO> getMostProductSale(LocalDate start, LocalDate end);
    List<ProductReportDTO> getMostCategorySale(LocalDate start, LocalDate end);
}
