package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.MovementsType;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Repository.Product.ProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import com.veloProWeb.Service.Report.IkardexService;
import com.veloProWeb.Service.User.Interface.IAlertService;
import com.veloProWeb.Validation.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductValidator validator;
    @Autowired private IkardexService kardexService;
    @Autowired private IAlertService alertService;

    /**
     * Creación de un nuevo producto
     * Válida los detalle del producto
     * Asigna valores predeterminados al nuevo producto
     * @param product - Producto con los detalles
     */
    @Override
    public void create(Product product) {
        validator.validateNewProduct(product);
        product.setId(null);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setBuyPrice(0);
        product.setSalePrice(0);
        product.setStock(0);
        product.setThreshold(0);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Creación Producto", MovementsType.AJUSTE);
    }

    /**
     * Actualiza detalle de un producto seleccionado
     * Verifica que si el producto tiene un stock mayor a 0 para darle un valor predeterminado como disponible
     * y si tiene un stock menor a 0 lo deja como no disponible predeterminado.
     * @param product - Producto a actualizar
     */
    @Override
    public void update(Product product){
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
     * @param product - producto para activar
     */
    @Override
    public void active(Product product) {
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Activado", MovementsType.AJUSTE);
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
        update(product);
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
        update(product);
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
     * Actualiza el estado del stock y reserva de un producto después de despacho creado.
     * Resta la cantidad con el stock actual del producto.
     * Suma la cantidad a la reserva del producto.
     * @param product - Producto seleccionado
     * @param quantity - cantidad a despachar
     */
    @Override
    public void updateStockAndReserveDispatch(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        product.setReserve(product.getReserve() + quantity);
        update(product);
    }

    /**
     * Cambia el estado del producto como Descontinuado
     * Asigna un estado negativo al producto para no estar disponible para ventas
     * @param product - producto a eliminar
     */
    @Override
    public void delete(Product product) {
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DESCONTINUADO);
        productRepo.save(product);
        kardexService.addKardex(product, 0, "Eliminado / Descontinuado", MovementsType.AJUSTE);
    }

    /**
     * Obtiene una lista del registro de todos los productos
     * @return - lista de productos
     */
    @Override
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    /**
     * Obtiene un producto específico
     * @param id - Identificador del producto a buscar
     * @return - Devuelve si se encontró un producto si no un valor vacío
     */
    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No ha seleccionado un producto registrado"));
    }


}
