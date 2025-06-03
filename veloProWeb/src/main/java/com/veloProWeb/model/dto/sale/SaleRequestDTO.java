package com.veloProWeb.model.dto.sale;

import com.veloProWeb.model.Enum.PaymentMethod;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SaleRequestDTO {
    private Long id;
    private LocalDate date;
    private Long idCustomer;
    private PaymentMethod paymentMethod;
    private int tax;
    private int total;
    private int discount;
    private int numberDocument;
    private String comment;
    private List<DetailSaleDTO> detailList;
}
