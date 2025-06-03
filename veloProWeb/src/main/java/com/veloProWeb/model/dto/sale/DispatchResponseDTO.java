package com.veloProWeb.model.dto.sale;

import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.DetailSaleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchResponseDTO {
    private Long id;
    private String trackingNumber;
    private DispatchStatus status;
    private String address;
    private String comment;
    private String customer;
    private boolean hasSale;
    private LocalDate created;
    private LocalDate deliveryDate;
    private List<DetailSaleDTO> detailSaleDTOS = new ArrayList<>();
}
