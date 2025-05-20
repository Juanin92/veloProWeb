package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.SupplierRequestDTO;
import com.veloProWeb.model.dto.purchase.SupplierResponseDTO;

import java.util.List;

public interface ISupplierService {

    void createSupplier(SupplierRequestDTO dto);
    void updateSupplier(SupplierRequestDTO dto);
    List<SupplierResponseDTO> getAll();
    SupplierResponseDTO getSupplierByRut(String rut);
}
