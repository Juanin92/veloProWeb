import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductDTO } from '../../models/DTO/product-dto';

@Injectable({
  providedIn: 'root'
})
export class StockService {

  private apiUrl = 'http://localhost:8080/stock';

  constructor(private httpClient: HttpClient) { }

  getProducts(): Observable<ProductDTO[]>{
    return this.httpClient.get<ProductDTO[]>(this.apiUrl);
  }
}
