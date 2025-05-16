import { Injectable } from '@angular/core';
import { Product } from '../models/Entity/Product/product';
import { ProductResponse } from '../models/Entity/Product/product-response';
import { ProductForm } from '../models/Entity/Product/product-form';
import { Brand } from '../models/Entity/Product/brand';
import { UnitProduct } from '../models/Entity/Product/unit-product';
import { Category } from '../models/Entity/Product/category';
import { Subcategory } from '../models/Entity/Product/subcategory';
import { ProductUpdateForm } from '../models/Entity/Product/product-update-form';

@Injectable({
  providedIn: 'root',
})
export class ProductMapperService {
  
  mapResponseToProduct(response: ProductResponse): Product {
    return {
      id: response.id,
      description: response.description,
      salePrice: response.salePrice,
      buyPrice: response.buyPrice,
      stock: response.stock,
      reserve: response.reserve,
      statusProduct: response.statusProduct,
      brand: response.brand,
      unit: response.unit,
      subcategoryProduct: response.subcategoryProduct,
      category: response.category,
    };
  }

  mapToProductForm(
    product: Product,
    brand: Brand,
    unit: UnitProduct,
    category: Category,
    subcategory: Subcategory
  ): ProductForm {
    return {
      description: product.description,
      brand: brand,
      unit: unit,
      category: category,
      subcategoryProduct: subcategory,
    };
  }

  mapProductToUpdate(product: Product): ProductUpdateForm{
    return {
      id: product.id,
      description: product.description,
      salePrice: product.salePrice,
      stock: product.stock
    }
  }
}
