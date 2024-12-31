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

  getProducts(): Observable<Product[]>{
    return this.httpClient.get<Product[]>(this.apiUrl);
  }

  createProduct(product: Product): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`,product);
  }
}
