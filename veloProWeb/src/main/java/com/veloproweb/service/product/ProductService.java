package com.veloproweb.service.product;

import com.veloproweb.exceptions.product.ProductNotFoundException;
import com.veloproweb.mapper.ProductMapper;
import com.veloproweb.model.dto.product.ProductRequestDTO;
import com.veloproweb.model.dto.product.ProductResponseDTO;
import com.veloproweb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.Enum.StatusProduct;
import com.veloproweb.repository.product.ProductRepo;
import com.veloproweb.service.product.interfaces.IProductService;
import com.veloproweb.validation.ProductValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final ProductEventService productEventService;
    private final ProductValidator validator;
    private final ProductMapper mapper;

    /**
     * Creación de un nuevo producto
     * @param dto - Producto con los detalles
     */
    @Transactional
    @Override
    public void create(ProductRequestDTO dto, UserDetails userDetails) {
        Product product = mapper.toEntity(dto);
        productRepo.save(product);
        productEventService.handleCreateRegister(product, "Creación Producto", userDetails);
    }

    /**
     * Actualiza la info de un producto.
     * Verifica si hubo cambio de stock para crear una alerta.
     * @param dto - producto con los datos actualizados
     */
    @Transactional
    @Override
    public void updateProductInfo(ProductUpdatedRequestDTO dto, UserDetails userDetails) {
        Product product = getProductById(dto.getId());
        int originalStock = product.getStock();
        validator.isDeleted(product);
        product.setDescription(dto.getDescription());
        product.setSalePrice(dto.getSalePrice());
        product.setStock(dto.getStock());
        productRepo.save(product);
        productEventService.isChangeStockOriginalValue(product, originalStock, dto, userDetails);
    }

    /**
     * Actualiza detalle de un producto seleccionado
     * Verifica que si el producto tiene un stock mayor a 0 para darle un valor predeterminado como disponible
     * y si tiene un stock menor a 0 lo deja como no disponible predeterminado.
     * @param product - Producto a actualizar
     */
    @Override
    public void updateStockStatus(Product product){
        if (product.getStock() > 0){
            product.setStatus(true);
            product.setStatusProduct(StatusProduct.DISPONIBLE);
            productRepo.save(product);
        }else {
            product.setStatus(false);
            product.setStatusProduct(StatusProduct.NODISPONIBLE);
            productRepo.save(product);
        }
    }

    /**
     * Activa un producto que no estaba disponible
     * Asigna un valor de status al producto como no disponible
     * @param dto - producto para activar
     */
    @Transactional
    @Override
    public void reactive(ProductUpdatedRequestDTO dto, UserDetails userDetails) {
        Product product = getProductById(dto.getId());
        validator.isActivated(product);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setStatus(true);
        productRepo.save(product);
        productEventService.handleCreateRegister(product, "Activado", userDetails);
    }

    /**
     * Cambia el estado del producto como Descontinuado
     * Asigna un estado negativo al producto para no estar disponible para ventas
     * @param dto - producto a eliminar
     */
    @Transactional
    @Override
    public void discontinueProduct(ProductUpdatedRequestDTO dto, UserDetails userDetails) {
        Product product = getProductById(dto.getId());
        validator.isDeleted(product);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DESCONTINUADO);
        productRepo.save(product);
        productEventService.handleCreateRegister(product, "Eliminado / Descontinuado", userDetails);
    }

    /**
     * Actualiza el estado del stock de un producto después de una compra del producto
     * Se suma el valor de la cantidad comprada con el stock registrado del producto
     * @param product - producto comprado
     * @param price - precio de compra
     * @param quantity - cantidad comprada
     */
    @Transactional
    @Override
    public void updateStockPurchase(Product product, int price, int quantity) {
        product.setBuyPrice(price);
        product.setStock(product.getStock() + quantity);
        updateStockStatus(product);
    }

    /**
     * Actualiza el estado del stock de un producto después de una venta del producto
     * Se resta la cantidad vendida con el stock registrado del producto
     * @param product - producto vendido
     * @param quantity - cantidad vendida
     */
    @Transactional
    @Override
    public void updateStockSale(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        updateStockStatus(product);
    }

    /**
     * Actualiza el estado del stock y reserva de un producto dependiendo del éxito del despacho.
     * Si exitoso se resta el stock y se suma reservar del producto,
     * si no suma el stock y resta la reserva del producto en caso contrario.
     * @param product - Producto seleccionado
     * @param quantity - cantidad a despachar
     */
    @Override
    public void updateStockAndReserveDispatch(Product product, int quantity,boolean success) {
        if (success) {
            product.setStock(product.getStock() - quantity);
            product.setReserve(product.getReserve() + quantity);
            updateStockStatus(product);
        }else{
            product.setStock(product.getStock() + quantity);
            product.setReserve(product.getReserve() - quantity);
            updateStockStatus(product);
        }
    }

    /**
     * Obtiene una lista del registro de todos los productos
     * @return - lista de productos
     */
    @Override
    public List<ProductResponseDTO> getAll() {
        return productRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Obtiene un producto específico
     * @param id - Identificador del producto a buscar
     * @return - Devuelve si se encontró un producto si no un valor vacío
     */
    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));
    }
}
