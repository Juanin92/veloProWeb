package com.veloProWeb.service.Sale;

import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.DetailSaleRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import com.veloProWeb.repository.DispatchRepo;
import com.veloProWeb.repository.Sale.SaleDetailRepo;
import com.veloProWeb.repository.Sale.SaleRepo;
import com.veloProWeb.service.customer.interfaces.ICustomerService;
import com.veloProWeb.service.product.interfaces.IProductService;
import com.veloProWeb.service.report.IKardexService;
import com.veloProWeb.service.Sale.Interface.IDispatchService;
import com.veloProWeb.service.Sale.Interface.ISaleDetailService;
import com.veloProWeb.service.Sale.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SaleDetailService implements ISaleDetailService {

    @Autowired private SaleDetailRepo saleDetailRepo;
    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private DispatchRepo dispatchRepo;
    @Autowired private SaleRepo saleRepo;
    @Autowired private IProductService productService;
    @Autowired private ICustomerService customerService;
    @Autowired private ISaleService saleService;
    @Autowired private IKardexService kardexService;
    @Autowired private IDispatchService dispatchService;

    /**
     * Crear detalle de ventas proporcionadas para una venta
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * Crea un registro del movimiento del producto
     * @param dtoList - Lista de objetos dto que contienen los detalles de la venta.
     * @param sale - Objeto que representa la venta asociada a los detalles.
     * @throws IllegalArgumentException Si no se encuentra un producto con el ID proporcionado en alguno de los detalles.
     */
    @Override
    public void createSaleDetailsToSale(List<DetailSaleDTO> dtoList, Sale sale, UserDetails userDetails) {
        for (DetailSaleDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setId(null);
            saleDetail.setQuantity(dto.getQuantity());
            saleDetail.setPrice((int) (product.getSalePrice() * 1.19));
            saleDetail.setTax((int) (product.getSalePrice() * 0.19));
            saleDetail.setTotal((int) ((product.getSalePrice() * 1.19) * dto.getQuantity()));
            saleDetail.setSale(sale);
            saleDetail.setDispatch(null);
            saleDetail.setProduct(product);
            saleDetailRepo.save(saleDetail);
            productService.updateStockSale(product, saleDetail.getQuantity());
            kardexService.addKardex(userDetails, product, dto.getQuantity(),
                    "Venta / " + sale.getDocument(), MovementsType.SALIDA);
        }
    }

    /**
     * Obtiene el registro de todos los detalles de ventas
     * @return - Lista con los detalles de ventas
     */
    @Override
    public List<SaleDetail> getAll() {
        return saleDetailRepo.findAll();
    }

    /**
     * Obtener detalle de venta de una venta específica.
     * Incluye los datos si esta venta tiene un cliente asociado para obtener tickets de la venta
     * y tener mayor detalle de esta venta del cliente sabiendo su estado y notificaciones del ticket
     * @param idSale - Identificador de la venta específica
     * @return - Lista de dto con detalles de la venta
     */
    @Override
    public List<DetailSaleRequestDTO> getSaleDetailsToSale(Long idSale) {
        List<DetailSaleRequestDTO> saleRequestDTOS = new ArrayList<>();
        List<SaleDetail> saleDetails = saleDetailRepo.findBySaleId(idSale);
        Optional<Sale> sale = saleService.getSaleById(idSale);
        String customerNames = "";
        boolean status = true;
        LocalDate notification = null;
        if (sale.isPresent()){
            if (sale.get().getCustomer() != null){
                Long idCustomer = sale.get().getCustomer().getId();
                Customer customer = customerService.getCustomerById(idCustomer);
                customerNames = customer.getName() + " " + customer.getSurname();
                List<TicketHistory> ticketHistoryList = ticketHistoryRepo.findByCustomerId(customer.getId());
                for (TicketHistory ticket : ticketHistoryList){
                    if (ticket.getDocument().equals(sale.get().getDocument())){
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
    public void createSaleDetailsToDispatch(List<DetailSaleDTO> dtoList, Dispatch dispatch) {
        for (DetailSaleDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setId(null);
            saleDetail.setQuantity(dto.getQuantity());
            saleDetail.setPrice((int) (product.getSalePrice() * 1.19));
            saleDetail.setTax((int) (product.getSalePrice() * 0.19));
            saleDetail.setTotal((int) ((product.getSalePrice() * 1.19) * dto.getQuantity()));
            saleDetail.setSale(null);
            saleDetail.setDispatch(dispatch);
            saleDetail.setProduct(product);
            saleDetailRepo.save(saleDetail);
            productService.updateStockAndReserveDispatch(product, saleDetail.getQuantity(), true);
        }
    }

    /**
     * Obtener detalle de venta de un despacho específico.
     * @param idDispatch - Identificador del despacho.
     * @return - Lista de dto con detalles de la venta
     */
    @Override
    public List<DetailSaleRequestDTO> getSaleDetailsToDispatch(Long idDispatch) {
        List<DetailSaleRequestDTO> saleRequestDTOS = new ArrayList<>();
        List<SaleDetail> saleDetails = saleDetailRepo.findByDispatchId(idDispatch);
        Optional<Dispatch> dispatch = dispatchService.getDispatchById(idDispatch);
        if (dispatch.isPresent()){
            for (SaleDetail saleDetail : saleDetails){
                DetailSaleRequestDTO dto = new DetailSaleRequestDTO();
                dto.setQuantity(saleDetail.getQuantity());
                dto.setPrice(saleDetail.getPrice());
                dto.setTax(saleDetail.getTax());
                dto.setDescriptionProduct(saleDetail.getProduct().getDescription());
                dto.setCustomer(null);
                dto.setTicketStatus(false);
                dto.setNotification(null);
                saleRequestDTOS.add(dto);
            }
        }
        return saleRequestDTOS;
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
        Optional<Dispatch> dispatch = dispatchService.getDispatchById(idDispatch);
        if (dispatch.isPresent()) {
            List<SaleDetail> saleDetails = saleDetailRepo.findByDispatchId(dispatch.get().getId());
            int totalTax = 0;
            for (SaleDetail saleDetail : saleDetails){
                saleDetail.setSale(sale);
                saleDetailRepo.save(saleDetail);
                totalTax += saleDetail.getTax() * saleDetail.getQuantity();
                productService.updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(), false);
                productService.updateStockSale(saleDetail.getProduct(), saleDetail.getQuantity());
            }
            dispatch.get().setHasSale(true);
            dispatchRepo.save(dispatch.get());
            sale.setTax(totalTax);
            saleRepo.save(sale);
        }else {
            throw new IllegalArgumentException("No se encontró el despacho");
        }
    }
}
