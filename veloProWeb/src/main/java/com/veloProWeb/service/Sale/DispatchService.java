package com.veloProWeb.service.Sale;

import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.DispatchDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.repository.DispatchRepo;
import com.veloProWeb.service.product.interfaces.IProductService;
import com.veloProWeb.service.Sale.Interface.IDispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DispatchService implements IDispatchService {

    @Autowired private DispatchRepo dispatchRepo;
    @Autowired private IProductService productService;
    private final Map<Integer, String> statusMap = new HashMap<>();

    public DispatchService(){
        statusMap.put(1, "En Preparación");
        statusMap.put(2, "En Ruta");
        statusMap.put(3, "Entregado");
        statusMap.put(4, "Eliminado");
    }

    /**
     * Obtiene una lista filtrada de registro de despacho
     * Despachos eliminados no se toman en cuenta
     * @return - Lista filtrada de despachos
     */
    @Override
    public List<DispatchDTO> getDispatches() {
        List<DispatchDTO> dtoList = new ArrayList<>();
        List<Dispatch> dispatchList = dispatchRepo.findAll();
        for (Dispatch dispatch : dispatchList){
            if (!dispatch.getStatus().equals("Eliminado")) {
                List<DetailSaleDTO> detailSaleDTOSList = new ArrayList<>();
                for(SaleDetail saleDetail : dispatch.getSaleDetails()){
                    DetailSaleDTO detailSaleDTO = new DetailSaleDTO();
                    detailSaleDTO.setId(saleDetail.getId());
                    detailSaleDTO.setIdProduct(saleDetail.getProduct().getId());
                    detailSaleDTO.setQuantity(saleDetail.getQuantity());
                    detailSaleDTOSList.add(detailSaleDTO);
                }
                DispatchDTO dto = new DispatchDTO(dispatch.getId(), dispatch.getTrackingNumber(), dispatch.getStatus(),
                        dispatch.getAddress(), dispatch.getComment(), dispatch.getCustomer(), dispatch.isHasSale(),
                        dispatch.getCreated(), dispatch.getDeliveryDate(), detailSaleDTOSList);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    /**
     * Creación de un despacho.
     * Verifica que el despacho no sea nulo y este contenga un objeto de Venta
     * @param dto - dto Despacho con los datos necesarios
     * @return - Objeto despacho con los datos necesario
     */
    @Override
    public Dispatch createDispatch(DispatchDTO dto) {
        if (dto != null) {
            Dispatch dispatch = new Dispatch();
            dispatch.setId(null);
            dispatch.setTrackingNumber("#" + (dispatchRepo.count() + 1));
            dispatch.setCreated(LocalDate.now());
            dispatch.setStatus(statusMap.get(1));
            dispatch.setAddress(dto.getAddress());
            dispatch.setComment(dto.getComment());
            dispatch.setCustomer(dto.getCustomer());
            dispatch.setDeliveryDate(null);
            dispatch.setHasSale(false);
            dispatchRepo.save(dispatch);
            return dispatch;
        }else {
            throw new IllegalArgumentException("Despacho debe tener datos");
        }
    }

    /**
     * Manejo del estado de un despacho.
     * Verifica que el despacho exista y que la acción exista.
     * @param dispatchID - Identificador del despacho
     * @param action - (número) Clave del Map para seleccionar estado
     */
    @Override
    public void handleStatus(Long dispatchID, int action) {
        Dispatch dispatch = getDispatchExisting(dispatchID);
        if (dispatch != null) {
            switch (action){
                case 1:
                    //Verifica que solo cuando sea "En Preparación" quede "En Ruta"
                    if (dispatch.getStatus().equals(statusMap.get(1))){
                        dispatch.setStatus(statusMap.get(2));
                    }
                    break;
                case 2:
                    // Verifica que solo cuando el estado sea diferente de "Eliminado" o "Entregado" quede "Eliminado"
                    if (!dispatch.getStatus().equals(statusMap.get(4)) && !dispatch.getStatus().equals(statusMap.get(3))){
                        dispatch.setStatus(statusMap.get(4));
                        for (SaleDetail saleDetail : dispatch.getSaleDetails()){
                            //Actualiza stock y reserva del producto eliminado del despacho
                            productService.updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(), false);
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Acción inválida");
            }
            dispatchRepo.save(dispatch);
        }else{
            throw new IllegalArgumentException("No se encontró el despacho");
        }
    }

    /**
     *  Actualiza el estado del despacho como "entregado".
     *  Verifica que el estado del despacho actual tenga una condición.
     *  Establece una fecha de entrega.
     * @param dispatchID - Identificador del despacho a actualizar
     */
    @Override
    public void handleDispatchReceiveToSale(Long dispatchID) {
        Dispatch dispatch = getDispatchExisting(dispatchID);
        if (dispatch != null) {
            if (dispatch.getStatus().equals(statusMap.get(1)) || dispatch.getStatus().equals(statusMap.get(2))){
                dispatch.setStatus(statusMap.get(3));
                dispatch.setDeliveryDate(LocalDate.now());
                dispatchRepo.save(dispatch);
            }
        }else {
            throw new IllegalArgumentException("No se encontró el despacho.");
        }
    }

    /**
     * Obtener un despacho por su identificador
     * @param id - Identificador del despacho a buscar
     * @return - Optional de despacho si se encuentra o null en el caso contrario
     */
    @Override
    public Optional<Dispatch> getDispatchById(Long id) {
        return dispatchRepo.findById(id);
    }

    /**
     * Obtiene un despacho existente en el registro
     * @param dispatchID - Identificador de despacho
     * @return - Si encuentra un despacho o valor nulo
     */
    private Dispatch getDispatchExisting(Long dispatchID){
        return dispatchRepo.findById(dispatchID).orElse(null);
    }
}
