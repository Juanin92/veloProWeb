import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Brand } from '../../models/Entity/Product/brand';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BrandService {

  private apiUrl = 'http://localhost:8080/marcas';
  
  constructor(private httpClient: HttpClient) { }

  getBrands(): Observable<Brand[]>{
    return this.httpClient.get<Brand[]>(this.apiUrl);
  }

  createBrand(brand: Brand): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`, brand);
  }
}
