import { Injectable } from '@angular/core';
import { Product } from '../../models/Entity/Product/product.model';
import { StatusProduct } from '../../models/enum/status-product';

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
        status: false,
        statusProduct: StatusProduct.UNAVAILABLE,
        brand: {id: 0, name: ''},
        unit: {id: 0, nameUnit: ''},
        subcategoryProduct: {id: 0, name: '', category: {id: 0, name: '', subcategory:[]}},
        category: {id: 0, name: '', subcategory:[]}
      };
    }
}
