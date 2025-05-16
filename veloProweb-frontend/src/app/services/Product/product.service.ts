import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../../models/Entity/Product/product';
import { AuthService } from '../User/auth.service';
import { ProductResponse } from '../../models/Entity/Product/product-response';
import { ProductForm } from '../../models/Entity/Product/product-form';
import { ProductUpdateForm } from '../../models/Entity/Product/product-update-form';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/stock';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene una lista de todos los productos desde el endpoint
   * @returns observable que emite una lista de productos
   */
  getProducts(): Observable<ProductResponse[]> {
    return this.httpClient.get<ProductResponse[]>(this.apiUrl);
  }

  /**
   * Crea un nuevo producto
   * @param brand - producto para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createProduct(product: ProductForm): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(
      `${this.apiUrl}`,
      product,
      { headers: this.auth.getAuthHeaders() }
    );
  }

  /**
   * Actualiza un producto realizando una petición PUT a la API
   * @param product - Producto a actualizar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  updateProduct(product: ProductUpdateForm): Observable<{ message: string }> {
    return this.httpClient.put<{ message: string }>(`${this.apiUrl}`, product, {
      headers: this.auth.getAuthHeaders(),
    });
  }

  /**
   * Eliminar un producto realizando una petición PUT a la API
   * @param product - Producto a eliminar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  deleteProduct(product: ProductUpdateForm): Observable<{ message: string }> {
    return this.httpClient.put<{ message: string }>(
      `${this.apiUrl}/eliminar_producto`,
      product,
      { headers: this.auth.getAuthHeaders() }
    );
  }

  /**
   * Activa un producto realizando una petición PUT a la API
   * @param product - Producto a activar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  activeProduct(product: ProductUpdateForm): Observable<{ message: string }> {
    return this.httpClient.put<{ message: string }>(
      `${this.apiUrl}/activar_producto`,
      product,
      { headers: this.auth.getAuthHeaders() }
    );
  }
}
