package com.veloProWeb.service.sale;

import com.veloProWeb.exceptions.sale.DispatchNotFoundException;
import com.veloProWeb.exceptions.sale.InvalidDispatchStatusException;
import com.veloProWeb.mapper.DispatchMapper;
import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.sale.DispatchRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.repository.Sale.DispatchRepo;
import com.veloProWeb.service.product.interfaces.IProductService;
import com.veloProWeb.service.sale.Interface.IDispatchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class DispatchService implements IDispatchService {

    private final DispatchRepo dispatchRepo;
    private final IProductService productService;
    private final DispatchMapper mapper;

    /**
     * Obtiene una lista filtrada de registro de despacho
     * Despachos eliminados no se toman en cuenta
     * @return - Lista filtrada de despachos
     */
    @Override
    public final List<DispatchResponseDTO> getDispatches() {
        return dispatchRepo.findByStatusNot(DispatchStatus.DELETED).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Creación de un despacho.
     * Verifica que el despacho no sea nulo y este contenga un objeto de Venta
     * @param dto - DTO Despacho con los datos necesarios
     * @return - Objeto despacho con los datos necesario
     */
    @Transactional
    @Override
    public final Dispatch createDispatch(DispatchRequestDTO dto) {
        Dispatch dispatch = mapper.toEntity(dto, dispatchRepo.count());
        dispatchRepo.save(dispatch);
        return dispatch;
    }

    /**
     * Manejo del estado de un despacho.
     * Verifica que el despacho exista y que la acción exista.
     * @param dispatchID - Identificador del despacho
     * @param statusAction - estado del despacho seleccionado a cambiar
     */
    @Transactional
    @Override
    public final void handleStatus(Long dispatchID, DispatchStatus statusAction) {
        Dispatch dispatch = getDispatchExisting(dispatchID);
        switch (statusAction){
            case DispatchStatus.IN_ROUTE:
                //Verifica que solo cuando sea "En Preparación" quede "En Ruta"
                if (dispatch.getStatus().equals(DispatchStatus.PREPARING)){
                    dispatch.setStatus(DispatchStatus.IN_ROUTE);
                }
                break;
            case DispatchStatus.DELETED:
                // Verifica que solo cuando el estado sea diferente de "Eliminado" o "Entregado" quede "Eliminado"
                if (!dispatch.getStatus().equals(DispatchStatus.DELETED) &&
                        !dispatch.getStatus().equals(DispatchStatus.DELIVERED)){
                    dispatch.setStatus(DispatchStatus.DELETED);
                    for (SaleDetail saleDetail : dispatch.getSaleDetails()){
                        //Actualiza stock y reserva del producto eliminado del despacho
                        productService.updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(),
                                false);
                    }
                }
                break;
            default:
                throw new InvalidDispatchStatusException("El estado escogido no es válido.");
        }
        dispatchRepo.save(dispatch);
    }

    /**
     *  Actualiza el estado del despacho como "entregado".
     *  Verifica que el estado del despacho actual tenga una condición.
     *  Establece una fecha de entrega.
     * @param dispatchID - Identificador del despacho a actualizar
     */
    @Transactional
    @Override
    public final void handleDispatchReceiveToSale(Long dispatchID) {
        Dispatch dispatch = getDispatchExisting(dispatchID);
        if (dispatch.getStatus().equals(DispatchStatus.PREPARING) ||
                dispatch.getStatus().equals(DispatchStatus.IN_ROUTE)){
            dispatch.setStatus(DispatchStatus.DELIVERED);
            dispatch.setDeliveryDate(LocalDate.now());
            dispatchRepo.save(dispatch);
        }
    }

    /**
     * Obtener un despacho por su identificador
     * @param id - Identificador del despacho a buscar
     * @return - Optional de despacho si se encuentra o null en el caso contrario
     */
    @Override
    public final Optional<Dispatch> getDispatchById(Long id) {
        return dispatchRepo.findById(id);
    }

    /**
     * Obtiene un despacho existente en el registro
     * @param dispatchID - Identificador de despacho
     * @return - Si encuentra un despacho o valor nulo
     */
    private Dispatch getDispatchExisting(Long dispatchID){
        return dispatchRepo.findById(dispatchID).orElseThrow(() -> new
                DispatchNotFoundException("No se encontró el despacho."));
    }
}
