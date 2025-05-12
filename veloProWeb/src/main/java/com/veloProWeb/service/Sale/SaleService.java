package com.veloProWeb.service.Sale;

import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.SaleRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.Enum.PaymentMethod;
import com.veloProWeb.repository.Sale.SaleRepo;
import com.veloProWeb.service.customer.CustomerService;
import com.veloProWeb.service.customer.TicketHistoryService;
import com.veloProWeb.service.Sale.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService implements ISaleService {

    @Autowired private SaleRepo saleRepo;
    @Autowired private TicketHistoryService ticketHistoryService;
    @Autowired private CustomerService customerService;

    /**
     * Crea una nueva venta.
     * @param dto - contiene los datos necesarios para crear una venta.
     * @return - objeto venta que representa la venta creada y persistida en la BD.
     */
    @Override
    public Sale createSale(SaleRequestDTO dto) {
        Sale sale = new Sale();
        sale.setId(null);
        sale.setDate(LocalDate.now());
        sale.setDocument("BO_" + dto.getNumberDocument());
        sale.setDiscount(dto.getDiscount());
        sale.setStatus(true);
        sale.setTax(dto.getTax());
        sale.setTotalSale(dto.getTotal());
        configureSaleByPaymentMethod(sale, dto);
        saleRepo.save(sale);
        return sale;
    }

    /**
     * Obtiene el número total de ventas registradas
     * @return - cantidad total de ventas
     */
    @Override
    public Long totalSales() {
        return saleRepo.count();
    }

    /**
     * Obtener todas las ventas registradas
     * Crea una lista que contendrá los datos en la lista del objeto dto
     * @return - La lista de objetos con los detalle de la venta
     */
    @Override
    public List<SaleRequestDTO> getAllSale() {
        List<Sale> saleList = saleRepo.findAll();
        List<SaleRequestDTO> dtoList = new ArrayList<>();
        for (Sale sale : saleList){
            SaleRequestDTO dto = convertToSaleRequestDTO(sale);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * Obtener una venta determinada por un ID
     * @param id - Identificador de la venta requerida
     * @return - Optional de venta encontrada
     */
    @Override
    public Optional<Sale> getSaleById(Long id) {
        return saleRepo.findById(id);
    }

    /**
     * Configura una Sale dependiendo el método de pago realizo en la venta.
     * Si el pago es mixto o préstamo se actualiza la deuda del cliente
     * y se agrega un ticket asociado al cliente con la venta
     * @param sale - Venta con los datos necesarios
     * @param dto - dto con los datos que proporcionan información para la venta
     * @throws IllegalArgumentException si el cliente especificado no existe en la BD
     */
    private void configureSaleByPaymentMethod(Sale sale,SaleRequestDTO dto) {
        switch (dto.getPaymentMethod()){
            case PRESTAMO -> {
                sale.setPaymentMethod(PaymentMethod.PRESTAMO);
                sale.setComment(null);
                Customer customer = customerService.getCustomerById(dto.getIdCustomer());
                sale.setCustomer(customer);
                customer.setTotalDebt(customer.getDebt() + dto.getTotal());
                customerService.updateTotalDebt(customer);
                customerService.addSaleToCustomer(customer);
                ticketHistoryService.addTicketToCustomer(customer, dto.getTotal());
                break;
            }
            case MIXTO -> {
                sale.setPaymentMethod(PaymentMethod.MIXTO);
                sale.setComment("Abono inicial: $" + dto.getComment());
                Customer customer = customerService.getCustomerById(dto.getIdCustomer());
                sale.setCustomer(customer);
                customer.setTotalDebt(customer.getDebt() + dto.getTotal());
                customerService.updateTotalDebt(customer);
                ticketHistoryService.addTicketToCustomer(customer, dto.getTotal());
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

    /**
     *  Convierte un objeto Sale a un dto
     * @param sale - la venta que se va convertir
     * @return - objeto dto que contiene los detalle de la venta
     */
    private SaleRequestDTO convertToSaleRequestDTO(Sale sale){
        SaleRequestDTO dto = new SaleRequestDTO();
        dto.setId(sale.getId());
        dto.setDate(sale.getDate());
        dto.setIdCustomer(sale.getCustomer() != null && sale.getCustomer().getId() != null ? sale.getCustomer().getId() : 0L);
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setTax(sale.getTax());
        dto.setTotal(sale.getTotalSale());
        dto.setDiscount(sale.getDiscount());
        dto.setComment(sale.getComment() + " # " + sale.getDocument());
        dto.setNumberDocument(0);

        List<DetailSaleDTO> detailSaleDTOS = new ArrayList<>();
        for (SaleDetail detail : sale.getSaleDetails()){
            DetailSaleDTO detailSaleDTO =  new DetailSaleDTO();
            detailSaleDTO.setId(detail.getId());
            detailSaleDTO.setIdProduct(detail.getProduct().getId());
            detailSaleDTO.setQuantity(detail.getQuantity());
            detailSaleDTOS.add(detailSaleDTO);
        }
        dto.setDetailList(detailSaleDTOS);
        return dto;
    }
}
