package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.sale.DispatchRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;

import java.util.List;
import java.util.Optional;

public interface IDispatchService {
    List<DispatchResponseDTO> getDispatches();
    Dispatch createDispatch(DispatchRequestDTO dto);
    void handleStatus(Long dispatchID, DispatchStatus statusAction);
    void handleDispatchReceiveToSale(Long dispatchID);
    Optional<Dispatch> getDispatchById(Long id);
}
