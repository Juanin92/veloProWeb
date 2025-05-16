package com.veloProWeb.service.Product;

import com.veloProWeb.exceptions.product.ProductNotFoundException;
import com.veloProWeb.mapper.ProductMapper;
import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductResponseDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.Product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.Enum.StatusProduct;
import com.veloProWeb.repository.Product.ProductRepo;
import com.veloProWeb.service.Product.Interfaces.IProductService;
import com.veloProWeb.service.Report.IkardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import com.veloProWeb.validation.ProductValidator;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final IkardexService kardexService;
    private final IAlertService alertService;
    private final ProductValidator validator;
    private final ProductMapper mapper;

    /**
     * Creación de un nuevo producto
     * @param dto - Producto con los detalles
     */
    @Transactional
    @Override
    public void create(ProductRequestDTO dto) {
        Product product = mapper.toEntity(dto);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setBuyPrice(0);
        product.setSalePrice(0);
        product.setStock(0);
        product.setThreshold(0);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Creación Producto", MovementsType.AJUSTE);
    }

    @Transactional
    @Override
    public void updateProductInfo(ProductUpdatedRequestDTO dto) {
        Product product = getProductById(dto.getId());
        boolean change = validator.isChangeStockOriginalValue(product, dto.getStock());
        validator.isDeleted(product);
        product.setDescription(dto.getDescription());
        product.setSalePrice(dto.getSalePrice());
        if (change) {
            String comment = String.format("%s - stock original: %s, stock nuevo: %s", dto.getComment(),
                    product.getStock(), dto.getStock());
            int quantity = product.getStock() - dto.getStock();
            kardexService.addKardex(product, quantity, comment, MovementsType.AJUSTE);
            alertService.createAlert(product, comment);
        }
        product.setStock(dto.getStock());
        productRepo.save(product);
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
            product.setThreshold(product.getThreshold());
            productRepo.save(product);
        }else {
            product.setStatus(false);
            product.setStatusProduct(StatusProduct.NODISPONIBLE);
            product.setThreshold(product.getThreshold());
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
    public void active(ProductUpdatedRequestDTO dto) {
        Product product = getProductById(dto.getId());
        validator.isActivated(product);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setStatus(true);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Activado", MovementsType.AJUSTE);
    }

    /**
     * Cambia el estado del producto como Descontinuado
     * Asigna un estado negativo al producto para no estar disponible para ventas
     * @param dto - producto a eliminar
     */
    @Transactional
    @Override
    public void delete(ProductUpdatedRequestDTO dto) {
        Product product = getProductById(dto.getId());
        validator.isDeleted(product);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DESCONTINUADO);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Eliminado / Descontinuado", MovementsType.AJUSTE);
    }

    /**
     * Actualiza el estado del stock de un producto después de una compra del producto
     * Se suma el valor de la cantidad comprada con el stock registrado del producto
     * @param product - producto comprado
     * @param price - precio de compra
     * @param quantity - cantidad comprada
     */
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
    @Override
    public void updateStockSale(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        updateStockStatus(product);
    }

    /**
     * Verifica y crea alertas para cada producto en intervalos regulares.
     * Crea una alerta si no hay stock disponible o umbral bajo y no existe ya una alerta activa.
     * Llama al servicio de Kardex para verificar y manejar las bajas ventas del producto.
     * @Scheduled - Está programado para actuar cada 6 hr automáticamente
     */
    @Override
    @Scheduled(fixedRate = 21600000)
    public void checkAndCreateAlertsByProduct() {
        List<Product> products =  productRepo.findAll();
        for (Product product : products){
            String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
            if (product.getStock() == 0 && !alertService.isAlertActive(product, noStockDescription)){
                alertService.createAlert(product, noStockDescription);
            }
            String criticalStockDescription = "Stock Crítico (" + product.getDescription() + " - " + product.getStock() + " unidades)";
            if (product.getStock() < product.getThreshold() && !alertService.isAlertActive(product, criticalStockDescription) ) {
                alertService.createAlert(product, criticalStockDescription);
            }
            kardexService.checkLowSales(product);
        }
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
