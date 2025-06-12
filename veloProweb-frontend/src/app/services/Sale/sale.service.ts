import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SaleRequestDTO } from '../../models/DTO/sale-request-dto';
import { AuthService } from '../user/auth.service';
import { SaleRequest } from '../../models/entity/sale/sale-request';
import { Sale } from '../../models/entity/sale/sale';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private apiUrl = 'http://localhost:8080/ventas';

  constructor(private http: HttpClient, private auth: AuthService) { }

  /**
     * Crea un nueva venta realizando una petición POST a la API
     * @param saleRequest - DTO con los valores de venta a agregar
     * @returns - Observable emite un mensaje de confirmación o error
     */
  createSale(saleRequest: SaleRequest): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}`, saleRequest, {headers: this.auth.getAuthHeaders()});
  }
  
  /**
   * Obtiene cantidad total de ventas realizadas haciendo una petición GET a la API
   * @returns - Observable emite un valor numérico
   */  
  getTotalSale(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}`);
  }

  /**
   * Obtiene todas la ventas registradas haciendo una petición GET 
   * @returns - Observable emite una lista DTO con los datos de ventas registradas
   */
  getAllSales(): Observable<Sale[]>{
    return this.http.get<Sale[]>(`${this.apiUrl}/lista-venta`, {headers: this.auth.getAuthHeaders()});
  }

  createSaleFromDispatch(saleRequest: SaleRequestDTO): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}/venta_despacho`, saleRequest, {headers: this.auth.getAuthHeaders()});
  }
}
