package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Product.*;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    /**
     * Válida los datos de un producto.
     * @param product - Producto seleccionado a validar.
     */
    public void validateNewProduct(Product product){
        validateBrand(product.getBrand());
        validateCategory(product.getCategory());
        validateSubcategory(product.getSubcategoryProduct());
        validateUnit(product.getUnit());
        validateDescription(product.getDescription());
    }

    /**
     * Válida el monto del stock, lanzará una excepción:
     * Si el monto es menor o igual a 0
     * @param stock - valor del monto
     */
    public void validateStock(int stock){
        if (stock <= 0){
            throw new IllegalArgumentException("Cantidad de ser mayor a 0");
        }
    }

    /**
     * Válida el monto del umbral, lanzará una excepción:
     * Si el monto es menor o igual a 0
     * @param number - valor del monto
     */
    public void validateThreshold(int number){
        if (number <= 0){
            throw new IllegalArgumentException("Cantidad de ser mayor a 0");
        }
    }

    /**
     * Válida el precio de venta, lanzará una excepción:
     * Si el monto es menor o igual 0
     * @param salePrice - valor del monto
     */
    public void validateSalePrice(int salePrice){
        if (salePrice <= 0){
            throw new IllegalArgumentException("Precio de ser mayor a 0");
        }
    }

    /**
     * Válida la marca de un producto, lanzará una excepción:
     *Si esta es nula.
     *@param brand - Objeto de una marca
     */
    private void validateBrand(BrandProduct brand){
        if (brand == null){
            throw new IllegalArgumentException("Seleccione una marca");
        }
    }

    /**
     * Válida la categoría de un producto, lanzará una excepción:
     * Si esta es nula.
     * @param category - Objeto de una categoría
     */
    private  void validateCategory(CategoryProduct category){
        if (category == null){
            throw new IllegalArgumentException("Seleccione una categoría");
        }
    }

    /**
     * Válida la subcategoría de un producto, lanzará una excepción:
     * Si esta es nula.
     * @param subcategory - Objeto de una subcategoría
     */
    private void validateSubcategory(SubcategoryProduct subcategory){
        if (subcategory == null){
            throw new IllegalArgumentException("Seleccione una subcategoría");
        }
    }

    /**
     * Válida la unidad de medida de un producto, lanzará una excepción:
     * Si la unidad es nula
     * @param unit - objeto de unidad
     */
    private void validateUnit(UnitProduct unit){
        if (unit == null){
            throw new IllegalArgumentException("Seleccione una unidad");
        }
    }

    /**
     * Válida la descripción de un producto, lanzará una excepción:
     * Si la cadena es nula o está vacía.
     * @param description - cadena que contiene el valor de la descripción
     */
    private void validateDescription(String description){
        if (description == null || description.trim().isBlank()){
            throw new IllegalArgumentException("Ingrese una descripción");
        }
    }
}
