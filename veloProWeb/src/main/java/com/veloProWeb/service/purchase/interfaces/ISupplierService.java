package com.veloProWeb.service.purchase.interfaces;

import com.veloProWeb.model.dto.purchase.SupplierRequestDTO;
import com.veloProWeb.model.dto.purchase.SupplierResponseDTO;
import com.veloProWeb.model.entity.purchase.Supplier;

import java.util.List;

public interface ISupplierService {

    void createSupplier(SupplierRequestDTO dto);
    void updateSupplier(SupplierRequestDTO dto);
    List<SupplierResponseDTO> getAll();
    SupplierResponseDTO getDtoByRut(String rut);
    Supplier getEntityByRut(String rut);
}
