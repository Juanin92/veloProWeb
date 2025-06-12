package com.veloProWeb.service.sale;

import com.veloProWeb.mapper.SaleMapper;
import com.veloProWeb.model.dto.sale.DetailSaleRequestDTO;
import com.veloProWeb.model.dto.sale.SaleDetailRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import com.veloProWeb.repository.Sale.DispatchRepo;
import com.veloProWeb.repository.Sale.SaleDetailRepo;
import com.veloProWeb.repository.Sale.SaleRepo;
import com.veloProWeb.service.customer.interfaces.ICustomerService;
import com.veloProWeb.service.product.interfaces.IProductService;
import com.veloProWeb.service.inventory.IKardexService;
import com.veloProWeb.service.sale.Interface.IDispatchService;
import com.veloProWeb.service.sale.Interface.ISaleDetailService;
import com.veloProWeb.service.sale.Interface.ISaleService;
import com.veloProWeb.validation.SaleValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SaleDetailService implements ISaleDetailService {

    private final SaleDetailRepo saleDetailRepo;
    private final TicketHistoryRepo ticketHistoryRepo;
    private final DispatchRepo dispatchRepo;
    private final SaleRepo saleRepo;
    private final IProductService productService;
    private final ICustomerService customerService;
    private final ISaleService saleService;
    private final IKardexService kardexService;
    private final IDispatchService dispatchService;
    private final SaleValidator validator;
    private final SaleMapper mapper;

    /**
     * Crear detalle de ventas proporcionadas para una venta
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * Crea un registro del movimiento del producto
     * @param dtoList - Lista de objetos dto que contienen los detalles de la venta.
     * @param sale - Objeto que representa la venta asociada a los detalles.
     * @param userDetails - detalles del usuario autenticado.
     */
    @Override
    public void addDetailsToSale(List<SaleDetailRequestDTO> dtoList, Sale sale, UserDetails userDetails) {
        validator.hasSale(sale);
        for (SaleDetailRequestDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = mapper.toSaleDetailEntityFromSale(dto, product, sale);
            saleDetailRepo.save(saleDetail);
            productService.updateStockSale(product, saleDetail.getQuantity());
            kardexService.addKardex(userDetails, product, dto.getQuantity(), "Venta " + sale.getDocument(),
                    MovementsType.SALIDA);
        }
    }

    /**
     * Obtener detalle de venta de una venta específica.
     * Incluye los datos si esta venta tiene un cliente asociado para obtener tickets de la venta
     * y tener mayor detalle de esta venta del cliente sabiendo su estado y notificaciones del ticket
     * @param idSale - Identificador de la venta específica
     * @return - Lista de dto con detalles de la venta
     */
    @Override
    public List<DetailSaleRequestDTO> getDetailsBySaleId(Long idSale) {
        List<DetailSaleRequestDTO> saleRequestDTOS = new ArrayList<>();
        List<SaleDetail> saleDetails = saleDetailRepo.findBySaleId(idSale);
        Sale sale = saleService.getSaleById(idSale);
        String customerNames = "";
        boolean status = true;
        LocalDate notification = null;
        if (sale != null){
            if (sale.getCustomer() != null){
                Long idCustomer = sale.getCustomer().getId();
                Customer customer = customerService.getCustomerById(idCustomer);
                customerNames = customer.getName() + " " + customer.getSurname();
                List<TicketHistory> ticketHistoryList = ticketHistoryRepo.findByCustomerId(customer.getId());
                for (TicketHistory ticket : ticketHistoryList){
                    if (ticket.getDocument().equals(sale.getDocument())){
                        status = ticket.isStatus();
                        notification = ticket.getNotificationsDate();
                    }
                }
            }
            for (SaleDetail saleDetail : saleDetails){
                DetailSaleRequestDTO dto = new DetailSaleRequestDTO();
                dto.setQuantity(saleDetail.getQuantity());
                dto.setPrice(saleDetail.getPrice());
                dto.setDescriptionProduct(saleDetail.getProduct().getDescription());
                dto.setCustomer(customerNames);
                dto.setTicketStatus(status);
                dto.setNotification(notification);
                dto.setHasDispatch(saleDetail.getDispatch() != null);
                saleRequestDTOS.add(dto);
            }
        }
        return saleRequestDTOS;
    }

    /**
     * Crear detalle de ventas proporcionadas para un despacho.
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * actualiza el stock y reserva del producto.
     * @param dtoList - Lista de objetos dto que contienen los detalles de la venta.
     * @param dispatch - Objeto que representa el despacho asociado a los detalles.
     */
    @Override
    public void createSaleDetailsToDispatch(List<SaleDetailRequestDTO> dtoList, Dispatch dispatch) {
        for (SaleDetailRequestDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = mapper.toSaleDetailEntityFromDispatch(dto, product, dispatch);
            saleDetailRepo.save(saleDetail);
            productService.updateStockAndReserveDispatch(product, saleDetail.getQuantity(), true);
        }
    }

    /**
     * Asocia una venta a los detalles de venta de un despacho.
     * Agregar la venta al detalle.
     * Actualiza el stock y reserva de los productos de la lista.
     * Marca el despacho como true por tener una venta asociada.
     * @param idDispatch - Identificador del despacho seleccionado
     * @param sale - Venta que se asociara al detalle de venta
     */
    @Override
    public void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale) {
        Dispatch dispatch = dispatchService.getDispatchById(idDispatch);
        List<SaleDetail> saleDetails = saleDetailRepo.findByDispatchId(dispatch.getId());
        int totalTax = 0;
        for (SaleDetail saleDetail : saleDetails){
            saleDetail.setSale(sale);
            saleDetailRepo.save(saleDetail);
            totalTax += saleDetail.getTax() * saleDetail.getQuantity();
            productService.updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(), false);
            productService.updateStockSale(saleDetail.getProduct(), saleDetail.getQuantity());
        }
        dispatch.setHasSale(true);
        dispatchRepo.save(dispatch);
        sale.setTax(totalTax);
        saleRepo.save(sale);
    }
}
