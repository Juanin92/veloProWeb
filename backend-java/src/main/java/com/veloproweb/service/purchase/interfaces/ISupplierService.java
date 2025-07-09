package com.veloproweb.service.purchase.interfaces;

import com.veloproweb.model.dto.purchase.SupplierRequestDTO;
import com.veloproweb.model.dto.purchase.SupplierResponseDTO;
import com.veloproweb.model.entity.purchase.Supplier;

import java.util.List;

public interface ISupplierService {

    void createSupplier(SupplierRequestDTO dto);
    void updateSupplier(SupplierRequestDTO dto);
    List<SupplierResponseDTO> getAll();
    SupplierResponseDTO getDtoByRut(String rut);
    Supplier getEntityByRut(String rut);
}
