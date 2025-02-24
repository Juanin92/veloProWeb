import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SaleRequestDTO } from '../../models/DTO/sale-request-dto';
import { DetailSaleRequestDTO } from '../../models/DTO/detail-sale-request-dto';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private apiUrl = 'http://localhost:8080/ventas';

  constructor(private http: HttpClient) { }

  /**
     * Crea un nueva venta realizando una petición POST a la API
     * @param saleRequest - DTO con los valores de venta a agregar
     * @returns - Observable emite un mensaje de confirmación o error
     */
    createSale(saleRequest: SaleRequestDTO): Observable<{message: string}>{
      return this.http.post<{message: string}>(`${this.apiUrl}`, saleRequest);
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
  getAllSales(): Observable<SaleRequestDTO[]>{
    return this.http.get<SaleRequestDTO[]>(`${this.apiUrl}/lista-venta`);
  }

  /**
   * Obtener detalle de ventas de una venta especifica mediante una petición GET
   * @param idSale - Identificador de la venta seleccionada
   * @returns - Observable emite una lista con los detalles de la venta
   */
  getDetailSale(idSale: number): Observable<DetailSaleRequestDTO[]>{
    return this.http.get<DetailSaleRequestDTO[]>(`${this.apiUrl}/detalles`, {
      params: {idSale: idSale.toString()}
    });
  }

  createSaleFromDispatch(saleRequest: SaleRequestDTO): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}/venta_despacho`, saleRequest);
  }
}
