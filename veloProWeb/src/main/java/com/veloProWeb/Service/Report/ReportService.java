package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.Report.DailySaleAvgDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleCountDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleEarningDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleSumDTO;
import com.veloProWeb.Repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService implements IReportService{

    @Autowired private ReportRepo reportRepo;

    @Override
    public List<DailySaleCountDTO> getDailySale(LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public List<DailySaleSumDTO> getTotalSaleDaily(LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public List<DailySaleAvgDTO> getAverageTotalSaleDaily(LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public List<DailySaleEarningDTO> getEarningSale(LocalDate start, LocalDate end) {
        return List.of();
    }
}
