import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../../models/Entity/Product/product.model';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8080/stock';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  /**
   * Obtiene una lista de todos los productos desde el endpoint
   * @returns observable que emite una lista de productos
   */
  getProducts(): Observable<Product[]>{
    return this.httpClient.get<Product[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Crea un nuevo producto
   * @param brand - producto para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`,product, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Actualiza un producto realizando una petición PUT a la API
   * @param product - Producto a actualizar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  updateProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}`, product, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Eliminar un producto realizando una petición PUT a la API
   * @param product - Producto a eliminar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  deleteProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar_producto`, product, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Activa un producto realizando una petición PUT a la API
   * @param product - Producto a activar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  activeProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar_producto`, product, {headers: this.auth.getAuthHeaders()});
  }
}
