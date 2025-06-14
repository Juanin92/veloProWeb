package com.veloproweb.service.sale.Interface;

import com.veloproweb.model.Enum.DispatchStatus;
import com.veloproweb.model.dto.sale.DispatchRequestDTO;
import com.veloproweb.model.dto.sale.DispatchResponseDTO;
import com.veloproweb.model.entity.Sale.Dispatch;

import java.util.List;

public interface IDispatchService {
    List<DispatchResponseDTO> getDispatches();
    Dispatch createDispatch(DispatchRequestDTO dto);
    void handleStatus(Long dispatchID, DispatchStatus statusAction);
    void handleDispatchReceiveToSale(Long dispatchID);
    Dispatch getDispatchById(Long id);
}
