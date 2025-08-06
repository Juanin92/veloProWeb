package com.veloproweb.service.sale;

import com.veloproweb.exceptions.sale.SaleNotFoundException;
import com.veloproweb.mapper.SaleMapper;
import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.model.dto.sale.SaleRequestDTO;
import com.veloproweb.model.dto.sale.SaleResponseDTO;
import com.veloproweb.model.entity.sale.Sale;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.repository.sale.SaleRepo;
import com.veloproweb.service.sale.interfaces.ISaleEventService;
import com.veloproweb.service.sale.interfaces.ISaleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SaleService implements ISaleService {

    private final SaleRepo saleRepo;
    private final ISaleEventService eventService;
    private final SaleMapper mapper;

    /**
     * Crea una nueva venta.
     *
     * @param dto - contiene los datos necesarios para crear una venta.
     * @return - objeto venta que representa la venta creada y persistida en la BD.
     */
    @Transactional
    @Override
    public Sale createSale(SaleRequestDTO dto) {
        Sale sale = buildSaleForPaymentMethod(dto);
        saleRepo.save(sale);
        return sale;
    }

    /**
     * Obtiene el número total de ventas registradas
     *
     * @return - cantidad total de ventas
     */
    @Override
    public Long totalSales() {
        return saleRepo.count();
    }

    /**
     * Obtener todas las ventas registradas
     * Crea una lista que contendrá los datos en la lista del objeto DTO
     *
     * @return - La lista de objetos con los detalle de la venta
     */
    @Override
    public List<SaleResponseDTO> getAllSales() {
        return saleRepo.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Obtener una venta determinada por un ID
     *
     * @param id - Identificador de la venta requerida
     * @return - Optional de venta encontrada
     */
    @Override
    public Sale getSaleById(Long id) {
        return saleRepo.findById(id).orElseThrow(() -> new SaleNotFoundException("Venta no encontrada"));
    }

    /**
     * Configura una sale dependiendo el método de pago realizo en la venta.
     * Si el pago es mixto o préstamo se actualiza la deuda del cliente
     * y se agrega un ticket asociado al cliente con la venta
     *
     * @param dto - DTO con los datos que proporcionan información para la venta
     */
    private Sale buildSaleForPaymentMethod(SaleRequestDTO dto) {
        String lastDocument = getLastSaleDocument();
        PaymentMethod paymentMethod = dto.getPaymentMethod();
        Customer customer = eventService.needsCustomer(paymentMethod, dto.getIdCustomer());
        String comment = switch (paymentMethod) {
            case PRESTAMO -> null;
            case MIXTO -> String.format("Abono inicial: $%s", dto.getComment());
            case DEBITO, CREDITO -> String.format("Comprobante: n°%s", dto.getComment());
            case TRANSFERENCIA -> String.format("Transferencia: n°%s", dto.getComment());
            case EFECTIVO -> String.format("Efectivo: $%s", dto.getComment());
        };
        Sale sale = mapper.toSaleEntity(dto, lastDocument, paymentMethod, comment, customer);

        if (paymentMethod == PaymentMethod.PRESTAMO) {
            eventService.createSaleLoanPaymentEvent(customer, dto.getTotal());
        } else if (paymentMethod == PaymentMethod.MIXTO) {
            eventService.createSaleMixPaymentEvent(customer, dto.getTotal());
        }

        return sale;
    }

    /**
     * Obtiene la última venta creada
     *
     * @return - venta creado
     */
    private String getLastSaleDocument() {
        return saleRepo.findLastCreated()
                .map(Sale::getDocument)
                .orElse("0000");
    }
}
