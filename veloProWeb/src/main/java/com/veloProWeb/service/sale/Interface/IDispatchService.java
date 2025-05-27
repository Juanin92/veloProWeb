package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.dto.DispatchDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;

import java.util.List;
import java.util.Optional;

public interface IDispatchService {
    List<DispatchDTO> getDispatches();
    Dispatch createDispatch(DispatchDTO dto);
    void handleStatus(Long dispatchID, int action);
    void handleDispatchReceiveToSale(Long dispatchID);
    Optional<Dispatch> getDispatchById(Long id);
}
