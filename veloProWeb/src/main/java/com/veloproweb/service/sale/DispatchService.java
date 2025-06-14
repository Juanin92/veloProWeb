package com.veloproweb.service.sale;

import com.veloproweb.exceptions.sale.DispatchNotFoundException;
import com.veloproweb.exceptions.sale.InvalidDispatchStatusException;
import com.veloproweb.mapper.DispatchMapper;
import com.veloproweb.model.Enum.DispatchStatus;
import com.veloproweb.model.dto.sale.DispatchRequestDTO;
import com.veloproweb.model.dto.sale.DispatchResponseDTO;
import com.veloproweb.model.entity.Sale.Dispatch;
import com.veloproweb.repository.Sale.DispatchRepo;
import com.veloproweb.service.product.interfaces.IProductService;
import com.veloproweb.service.sale.Interface.IDispatchService;
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
    public List<DispatchResponseDTO> getDispatches() {
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
    public Dispatch createDispatch(DispatchRequestDTO dto) {
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
    public void handleStatus(Long dispatchID, DispatchStatus statusAction) {
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
                    //Actualiza stock y reserva del producto eliminado del despacho
                    dispatch.getSaleDetails().forEach(saleDetail ->
                            productService.updateStockAndReserveDispatch(saleDetail.getProduct(),
                                    saleDetail.getQuantity(), false));
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
    public void handleDispatchReceiveToSale(Long dispatchID) {
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
    public Dispatch getDispatchById(Long id) {
        return dispatchRepo.findById(id).orElseThrow(() -> new
                DispatchNotFoundException("No se encontró el despacho."));
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
