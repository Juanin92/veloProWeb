package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.Entity.Sale.Dispatch;
import com.veloProWeb.Repository.DispatchRepo;
import com.veloProWeb.Service.Sale.Interface.IDispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispatchService implements IDispatchService {

    @Autowired private DispatchRepo dispatchRepo;
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
    public List<Dispatch> getDispatches() {
        List<Dispatch> dispatchList = dispatchRepo.findAll();
        return dispatchList.stream()
                .filter(dispatch -> !dispatch.getStatus().equals("Eliminado"))
                .toList();
    }

    /**
     * Creación de un despacho.
     * Verifica que el despacho no sea nulo y este contenga un objeto de Venta
     * @param dispatch - Despacho con los datos necesarios
     */
    @Override
    public void createDispatch(Dispatch dispatch) {
        if (dispatch != null) {
            if (dispatch.getSale() != null){
                dispatch.setId(null);
                dispatch.setTrackingNumber("#" + dispatch.getId() + 1);
                dispatch.setCreated(LocalDate.now());
                dispatch.setStatus(statusMap.get(1));
                dispatch.setDeliveryDate(null);
                dispatchRepo.save(dispatch);
            }else {
                throw new IllegalArgumentException("Debe tener una venta asociada");
            }
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
                case 2:
                    //Verifica que solo cuando sea "En Preparación" quede "En Ruta"
                    if (dispatch.getStatus().equals(statusMap.get(1))){
                        dispatch.setStatus(statusMap.get(2));
                    }
                    break;
                case 3:
                    //Verifica que solo cuando sea "En Preparación" o "En Ruta" quede "Entregado"
                    if (dispatch.getStatus().equals(statusMap.get(1)) || dispatch.getStatus().equals(statusMap.get(2))){
                        dispatch.setStatus(statusMap.get(3));
                        dispatch.setDeliveryDate(LocalDate.now());
                    }
                    break;
                case 4:
                    //Verifica que solo cuando sea diferente "Eliminado" quede "Eliminado"
                    if (!dispatch.getStatus().equals(statusMap.get(4))){
                        dispatch.setStatus(statusMap.get(4));
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
     * Obtiene un despacho existente en el registro
     * @param dispatchID - Identificador de despacho
     * @return - Si encuentra un despacho o valor nulo
     */
    private Dispatch getDispatchExisting(Long dispatchID){
        return dispatchRepo.findById(dispatchID).orElse(null);
    }
}
