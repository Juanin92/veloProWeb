package com.veloproweb.service.purchase;

import com.veloproweb.exceptions.supplier.SupplierNotFoundException;
import com.veloproweb.mapper.SupplierMapper;
import com.veloproweb.model.dto.purchase.SupplierRequestDTO;
import com.veloproweb.model.dto.purchase.SupplierResponseDTO;
import com.veloproweb.model.entity.purchase.Supplier;
import com.veloproweb.repository.purchase.SupplierRepo;
import com.veloproweb.service.purchase.interfaces.ISupplierService;
import com.veloproweb.validation.SupplierValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepo supplierRepo;
    private final SupplierValidator validator;
    private final SupplierMapper mapper;

    /**
     * Crea un nuevo proveedor
     * Se valida que el proveedor no exista un registro de él, si no lanzara excepción
     * @param dto - Objeto que contiene los valores necesarios
     */
    @Transactional
    @Override
    public void createSupplier(SupplierRequestDTO dto) {
        Supplier supplierDB = getSupplierCreated(dto.getRut());
        validator.validateSupplierDoesNotExist(supplierDB);
        Supplier supplier = mapper.toEntity(dto);
        supplierRepo.save(supplier);
    }

    /**
     * Actualiza los datos de un proveedor
     *
     * @param dto - Objeto que contiene los valores necesarios
     */
    @Transactional
    @Override
    public void updateSupplier(SupplierRequestDTO dto) {
        Supplier supplierExists = getSupplierCreated(dto.getRut());
        validator.validateSupplierExists(supplierExists);
        mapper.updateSupplierFromDto(supplierExists, dto);
        Optional.of(supplierExists)
                        .ifPresent(supplierRepo::save);
    }

    /**
     * Obtiene el registro de todos los proveedores
     *
     * @return - Lista con los proveedores encontrados
     */
    @Override
    public List<SupplierResponseDTO> getAll() {
        return supplierRepo.findAll().stream()
                .map(mapper::responseDTO)
                .toList();
    }

    /**
     * Busca un proveedor ya creado por su RUT
     * @param rut - rut del proveedor
     * @return - DTO de proveedor o una excepción
     */
    @Override
    public SupplierResponseDTO getDtoByRut(String rut) {
        return supplierRepo.findByRut(rut)
                .map(mapper::responseDTO)
                .orElseThrow(() -> new SupplierNotFoundException("No existe registro del proveedor"));
    }

    /**
     * Busca un proveedor por su RUT
     * @param rut - rut del proveedor a buscar
     * @return - proveedor encontrado o una excepción
     */
    @Override
    public Supplier getEntityByRut(String rut) {
        return supplierRepo.findByRut(rut)
                .orElseThrow(() -> new SupplierNotFoundException("No existe registro del proveedor"));
    }

    /**
     * Busca un proveedor ya creado por su RUT
     * @param rut - rut del proveedor
     * @return - proveedor encontrado o null si no encuentra similitud
     */
    private Supplier getSupplierCreated(String rut){
        return supplierRepo.findByRut(rut).orElse(null);
    }
}
