package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.DTO.DispatchDTO;
import com.veloProWeb.Model.Entity.Sale.Dispatch;

import java.util.List;
import java.util.Optional;

public interface IDispatchService {
    List<DispatchDTO> getDispatches();
    Dispatch createDispatch(DispatchDTO dto);
    void handleStatus(Long dispatchID, int action);
    void handleDispatchReceiveToSale(Long dispatchID);
    Optional<Dispatch> getDispatchById(Long id);
}
