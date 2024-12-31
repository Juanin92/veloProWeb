import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../../models/Entity/Product/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8080/stock';

  constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene una lista de todos los productos desde el endpoint
   * @returns observable que emite una lista de productos
   */
  getProducts(): Observable<Product[]>{
    return this.httpClient.get<Product[]>(this.apiUrl);
  }

  /**
   * Crea un nuevo producto
   * @param brand - producto para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`,product);
  }
}
