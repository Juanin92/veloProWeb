package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Repository.Product.ProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import com.veloProWeb.Validation.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductValidator validator;

    /**
     * Creación de un nuevo producto
     * Válida los detalle del producto
     * Asigna valores predeterminados al nuevo producto
     * @param product - Producto con los detalles
     */
    @Override
    public void create(Product product) {
        validator.validateNewProduct(product);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setBuyPrice(0);
        product.setSalePrice(0);
        product.setStock(0);
        productRepo.save(product);
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
     * @param product - producto para activar
     */
    @Override
    public void active(Product product) {
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        productRepo.save(product);
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
     * Cambia el estado del producto como Descontinuado
     * Asigna un estado negativo al producto para no estar disponible para ventas
     * @param product - producto a eliminar
     */
    @Override
    public void delete(Product product) {
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DESCONTINUADO);
        productRepo.save(product);
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
        return productRepo.findById(id).orElse(null);
    }
}
