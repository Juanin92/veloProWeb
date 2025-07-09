package com.veloproweb.service.sale;

import com.veloproweb.mapper.SaleMapper;
import com.veloproweb.model.dto.sale.SaleDetailRequestDTO;
import com.veloproweb.model.dto.sale.SaleDetailResponseDTO;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.entity.sale.Dispatch;
import com.veloproweb.model.entity.sale.Sale;
import com.veloproweb.model.entity.sale.SaleDetail;
import com.veloproweb.model.Enum.MovementsType;
import com.veloproweb.repository.sale.DispatchRepo;
import com.veloproweb.repository.sale.SaleDetailRepo;
import com.veloproweb.service.product.interfaces.IProductService;
import com.veloproweb.service.inventory.IKardexService;
import com.veloproweb.service.sale.interfaces.IDispatchService;
import com.veloproweb.service.sale.interfaces.ISaleDetailService;
import com.veloproweb.validation.SaleValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SaleDetailService implements ISaleDetailService {

    private final SaleDetailRepo saleDetailRepo;
    private final DispatchRepo dispatchRepo;
    private final IProductService productService;
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
    @Transactional
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
    public List<SaleDetailResponseDTO> getDetailsBySaleId(Long idSale) {
        return saleDetailRepo.findBySaleId(idSale).stream()
                .map(mapper::toDetailResponseDTO)
                .toList();
    }

    /**
     * Crear detalle de ventas proporcionadas para un despacho.
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * actualiza el stock y reserva del producto.
     * @param dtoList - Lista de objetos dto que contienen los detalles de la venta.
     * @param dispatch - Objeto que representa el despacho asociado a los detalles.
     */
    @Transactional
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
    @Transactional
    @Override
    public void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale) {
        Dispatch dispatch = dispatchService.getDispatchById(idDispatch);
        List<SaleDetail> saleDetails = saleDetailRepo.findByDispatchId(dispatch.getId());
        for (SaleDetail saleDetail : saleDetails){
            saleDetail.setSale(sale);
            saleDetailRepo.save(saleDetail);
            productService.updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(), false);
            productService.updateStockSale(saleDetail.getProduct(), saleDetail.getQuantity());
        }
        dispatch.setHasSale(true);
        dispatchRepo.save(dispatch);
    }
}
