import { Injectable } from '@angular/core';
import { Product } from '../../models/entity/product/product';
import { StatusProduct } from '../../models/enum/status-product';
import { Brand } from '../../models/entity/product/brand';
import { UnitProduct } from '../../models/entity/product/unit-product';
import { Subcategory } from '../../models/entity/product/subcategory';
import { Category } from '../../models/entity/product/category';
import { ProductForm } from '../../models/entity/product/product-form';
import { ProductUpdateForm } from '../../models/entity/product/product-update-form';

@Injectable({
  providedIn: 'root'
})
export class ProductHelperService {

  /**
     * Crea un producto con valores predeterminados
     * Sirve para resetear los valores de objeto o evitar errores por ser null
     * @returns Producto con valores predeterminados
     */
  createEmptyProduct(): Product {
    return {
      id: 0,
      description: '',
      salePrice: 0,
      buyPrice: 0,
      stock: 0,
      reserve: 0,
      statusProduct: StatusProduct.UNAVAILABLE,
      brand: '',
      unit: '',
      subcategoryProduct: '',
      category: ''
    };
  }

  /**
   * Crea una marca con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns Marca con valores predeterminados
   */
  createEmptyBrand(): Brand {
    return {
      id: 0,
      name: ''
    }
  }

  /**
   * Crea una categoría con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns categoría con valores predeterminados
   */
  createEmptyCategory(): Category {
    return {
      id: 0,
      name: ''
    }
  }

  /**
   * Crea una subcategoría con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns subcategoría con valores predeterminados
   */
  createEmptySubcategory(): Subcategory {
    return {
      id: 0,
      name: '',
      category: this.createEmptyCategory()
    }
  }

  /**
   * Crea una unidad de medida con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns unidad de medida con valores predeterminados
   */
  createEmptyUnit(): UnitProduct {
    return {
      id: 0,
      nameUnit: ''
    }
  }

  createEmptyProductForm(): ProductForm{
    return {
      description: '',
      brand: this.createEmptyBrand(),
      unit: this.createEmptyUnit(),
      category: this.createEmptyCategory(),
      subcategoryProduct: this.createEmptySubcategory()
    }
  }

  createEmptyProductUpdateForm(): ProductUpdateForm{
    return {
      id: 0,
      description: '',
      salePrice: 0,
      stock: 0,
      comment: ''
    }
  }
}
