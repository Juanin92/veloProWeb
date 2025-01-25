package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.DTO.DetailSaleRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Customer.TicketHistoryRepo;
import com.veloProWeb.Repository.Sale.SaleDetailRepo;
import com.veloProWeb.Service.Customer.CustomerService;
import com.veloProWeb.Service.Customer.TicketHistoryService;
import com.veloProWeb.Service.Product.ProductService;
import com.veloProWeb.Service.Sale.Interface.ISaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SaleDetailService implements ISaleDetailService {

    @Autowired private SaleDetailRepo saleDetailRepo;
    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private ProductService productService;
    @Autowired private CustomerService customerService;
    @Autowired private SaleService saleService;

    /**
     * Crear detalle de ventas proporcionadas
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * @param dtoList - Lista de objetos DTO que contienen los detalles de la venta.
     * @param sale - Objeto que representa la venta asociada a los detalles.
     * @throws IllegalArgumentException Si no se encuentra un producto con el ID proporcionado en alguno de los detalles.
     */
    @Override
    public void createSaleDetails(List<DetailSaleDTO> dtoList, Sale sale) {
        for (DetailSaleDTO dto : dtoList) {
            Product product = productService.getProductById(dto.getIdProduct());
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setId(null);
            saleDetail.setQuantity(dto.getQuantity());
            saleDetail.setPrice((int) (product.getSalePrice() * 1.19));
            saleDetail.setTax((int) (product.getSalePrice() * 0.19));
            saleDetail.setTotal((int) ((product.getSalePrice() * 1.19) * 2));
            saleDetail.setSale(sale);
            saleDetail.setProduct(product);
            saleDetailRepo.save(saleDetail);
            productService.updateStockSale(product, saleDetail.getQuantity());
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
     * @return - Lista de DTO con detalles de la venta
     */
    @Override
    public List<DetailSaleRequestDTO> getSaleDetails(Long idSale) {
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
                saleRequestDTOS.add(dto);
            }
        }
        return saleRequestDTOS;
    }
}
