package com.veloProWeb.Service.SaleService;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Enum.PaymentMethod;
import com.veloProWeb.Repository.Sale.SaleRepo;
import com.veloProWeb.Service.Customer.TicketHistoryService;
import com.veloProWeb.Service.SaleService.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SaleService implements ISaleService {

    @Autowired private SaleRepo saleRepo;
    @Autowired private TicketHistoryService ticketHistoryService;

    @Override
    public void addSale(Sale sale) {
        saleRepo.save(sale);
    }

    @Override
    public Sale addSale(int discount, int total, List<DetailSaleDTO> dto, int discountAmount, boolean isSelected, Customer customer, Long numberSale, int comment, PaymentMethod active) {
        Sale sale = new Sale();
        sale.setDiscount(discount);
        sale.setTotalSale(total - discount);

        int totalTax;
        if (isSelected) {
            totalTax = calculateTaxDiscount(dto, discountAmount);
        }else {
            totalTax = calculateTax(dto);
        }
        sale.setTax(totalTax);
        configurePaymentMethod(sale, customer, total, numberSale, comment, active);
        sale.setDate(LocalDate.now());
        sale.setDocument("BO_" + totalSales());
        sale.setStatus(true);
        return sale;
    }

    @Override
    public Long totalSales() {
        return saleRepo.count();
    }

    @Override
    public Optional<Sale> getSaleById(Long id) {
        return saleRepo.findById(id);
    }

    @Override
    public List<Sale> getAll() {
        return saleRepo.findAll();
    }

    @Override
    public int calculateDiscountSale(int total, int amount) {
        if (amount >= 0 && amount < 100) {
            return (total * amount) / 100;
        }
        return 0;
    }

    @Override
    public int calculateToSale(List<DetailSaleDTO> dtoList) {
        int total = 0;
        for (DetailSaleDTO dto : dtoList){
            int newTotal = dto.getSalePrice() * dto.getQuantity();
            total += newTotal;
        }
        return total;
    }

    @Override
    public int calculateTax(List<DetailSaleDTO> dtoList) {
        return dtoList.stream().mapToInt(dto -> dto.getTax() * dto.getQuantity()).sum();
    }

    @Override
    public int calculateTaxDiscount(List<DetailSaleDTO> dtoList, int discount) {
        int totalTax = calculateTax(dtoList);
        return totalTax - (totalTax * discount / 100);
    }

    @Override
    public void saleRegisterVoid(Sale sale) {
        sale.setStatus(false);
        if (sale.getComment() == null) {
            sale.setComment("ANULADO");
        }else{
            sale.setComment("ANULADO - " + sale.getComment());
        }
        saleRepo.save(sale);
    }

    @Override
    public void configurePaymentMethod(Sale sale, Customer customer, int total, Long numberSale, int comment, PaymentMethod active) {
        switch (active){
            case PRESTAMO -> {
                sale.setPaymentMethod(PaymentMethod.PRESTAMO);
                sale.setCustomer(customer);
                sale.setComment(null);
                customer.setTotalDebt(customer.getDebt() + total);
                ticketHistoryService.AddTicketToCustomer(customer, numberSale, total, LocalDate.now());
                break;
            }
            case MIXTO -> {
                sale.setPaymentMethod(PaymentMethod.MIXTO);
                sale.setCustomer(customer);
                sale.setComment("Abono inicial: $" + comment);
                customer.setTotalDebt(customer.getDebt() + total);
                ticketHistoryService.AddTicketToCustomer(customer, numberSale, total, LocalDate.now());
                break;
            }
            case DEBITO -> {
                sale.setPaymentMethod(PaymentMethod.DEBITO);
                sale.setCustomer(null);
                break;
            }
            case CREDITO -> {
                sale.setPaymentMethod(PaymentMethod.CREDITO);
                sale.setCustomer(null);
                break;
            }
            case TRANSFERENCIA -> {
                sale.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
                sale.setCustomer(null);
                break;
            }
            case EFECTIVO -> {
                sale.setPaymentMethod(PaymentMethod.EFECTIVO);
                sale.setCustomer(null);
                sale.setComment(null);
                break;
            }
        }
    }
}
