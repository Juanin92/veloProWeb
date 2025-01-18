package com.veloProWeb.Service.SaleService.Interface;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Enum.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface ISaleService {
    void addSale(Sale sale);
    Sale addSale(int discount, int total, List<DetailSaleDTO> dto, int discountAmount, boolean isSelected,
                 Customer customer, Long numberSale, int comment, PaymentMethod active);
    Long totalSales();
    Optional<Sale> getSaleById(Long id);
    List<Sale> getAll();
    int calculateDiscountSale(int total, int amount);
    int calculateToSale(List<DetailSaleDTO> dtoList);
    int calculateTax(List<DetailSaleDTO> dtoList);
    int calculateTaxDiscount(List<DetailSaleDTO> dtoList, int discount);
    void saleRegisterVoid(Sale sale);
    void configurePaymentMethod(Sale sale, Customer customer, int total, Long numberSale, int comment, PaymentMethod active);
}
