package com.veloproweb.model.dto.sale;

import com.veloproweb.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponseDTO {
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private String document;
    private String comment;
    private int discount;
    private int tax;
    private int totalSale;
    private boolean status;
    private String customer;
    private LocalDate notification;
    private boolean ticketStatus;
    private List<SaleDetailResponseDTO> saleDetails;
}
