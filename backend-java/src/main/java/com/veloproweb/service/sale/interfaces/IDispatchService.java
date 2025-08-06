package com.veloproweb.service.sale.interfaces;

import com.veloproweb.model.enums.DispatchStatus;
import com.veloproweb.model.dto.sale.DispatchRequestDTO;
import com.veloproweb.model.dto.sale.DispatchResponseDTO;
import com.veloproweb.model.entity.sale.Dispatch;

import java.util.List;

public interface IDispatchService {
    List<DispatchResponseDTO> getDispatches();
    Dispatch createDispatch(DispatchRequestDTO dto);
    void handleStatus(Long dispatchID, DispatchStatus statusAction);
    void handleDispatchReceiveToSale(Long dispatchID);
    Dispatch getDispatchById(Long id);
}
