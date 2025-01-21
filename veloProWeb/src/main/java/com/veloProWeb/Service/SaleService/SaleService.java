package com.veloProWeb.Service.SaleService;

import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Enum.PaymentMethod;
import com.veloProWeb.Repository.Sale.SaleRepo;
import com.veloProWeb.Service.Customer.CustomerService;
import com.veloProWeb.Service.Customer.TicketHistoryService;
import com.veloProWeb.Service.SaleService.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SaleService implements ISaleService {

    @Autowired private SaleRepo saleRepo;
    @Autowired private TicketHistoryService ticketHistoryService;
    @Autowired private CustomerService customerService;

    @Override
    public Sale createSale(SaleRequestDTO dto) {
        Sale sale = new Sale();
        sale.setId(null);
        sale.setDate(dto.getDate());
        sale.setDocument("BO_" + dto.getNumberDocument());
        sale.setDiscount(dto.getDiscount());
        sale.setStatus(true);
        sale.setTax(dto.getTax());
        sale.setTotalSale(dto.getTotal());
        configureSaleByPaymentMethod(sale, dto);
        saleRepo.save(sale);
        return sale;
    }

    @Override
    public Long totalSales() {
        return saleRepo.count();
    }

//    @Override
//    public Optional<Sale> getSaleById(Long id) {
//        return saleRepo.findById(id);
//    }
//
//    @Override
//    public List<Sale> getAll() {
//        return saleRepo.findAll();
//    }
//    @Override
//    public void saleRegisterVoid(Sale sale) {
//        sale.setStatus(false);
//        if (sale.getComment() == null) {
//            sale.setComment("ANULADO");
//        }else{
//            sale.setComment("ANULADO - " + sale.getComment());
//        }
//        saleRepo.save(sale);
//    }
//

    private void configureSaleByPaymentMethod(Sale sale,SaleRequestDTO dto) {
        switch (dto.getPaymentMethod()){
            case PRESTAMO -> {
                sale.setPaymentMethod(PaymentMethod.PRESTAMO);
                sale.setComment(null);
                Customer customer = customerService.getCustomerById(dto.getIdCustomer());
                sale.setCustomer(customer);
                customer.setTotalDebt(customer.getDebt() + dto.getTotal());
                customerService.updateTotalDebt(customer);
                ticketHistoryService.AddTicketToCustomer(customer, (long) dto.getNumberDocument(), dto.getTotal(), LocalDate.now());
                break;
            }
            case MIXTO -> {
                sale.setPaymentMethod(PaymentMethod.MIXTO);
                sale.setComment("Abono inicial: $" + dto.getComment());
                Customer customer = customerService.getCustomerById(dto.getIdCustomer());
                sale.setCustomer(customer);
                customer.setTotalDebt(customer.getDebt() + dto.getTotal());
                customerService.updateTotalDebt(customer);
                ticketHistoryService.AddTicketToCustomer(customer, (long) dto.getNumberDocument(), dto.getTotal(), LocalDate.now());
                break;
            }
            case DEBITO -> {
                sale.setPaymentMethod(PaymentMethod.DEBITO);
                sale.setCustomer(null);
                sale.setComment("Comprobante: n°" + dto.getComment());
                break;
            }
            case CREDITO -> {
                sale.setPaymentMethod(PaymentMethod.CREDITO);
                sale.setCustomer(null);
                sale.setComment("Comprobante: n°" + dto.getComment());
                break;
            }
            case TRANSFERENCIA -> {
                sale.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
                sale.setCustomer(null);
                sale.setComment("Transferencia: n°" + dto.getComment());
                break;
            }
            case EFECTIVO -> {
                sale.setPaymentMethod(PaymentMethod.EFECTIVO);
                sale.setCustomer(null);
                sale.setComment("Efectivo: $" + dto.getComment());
                break;
            }
        }
    }
}
