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

  /**
   * Obtiene una lista de todas las marcas desde el endpoint
   * @returns observable que emite una lista de marcas
   */
  getBrands(): Observable<Brand[]>{
    return this.httpClient.get<Brand[]>(this.apiUrl);
  }

  /**
   * Crea una nueva marca
   * @param brand - marca para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createBrand(brand: Brand): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}`, brand);
  }
}
