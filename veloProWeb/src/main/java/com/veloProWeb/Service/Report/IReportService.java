package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.Report.DailySaleAvgDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleCountDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleEarningDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleSumDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {
    List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end);
    List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end);
    List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end);
//    List<ProductSaleDTO> getMostProductSale(LocalDate start, LocalDate end);
//    List<CategoriesSaleDTO> getMostCategorySale(LocalDate start, LocalDate end);
}
