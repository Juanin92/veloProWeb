import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PurchaseRequestDTO } from '../../models/DTO/purchase-request-dto';
import { DetailPurchaseRequestDTO } from '../../models/DTO/detail-purchase-request-dto';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private apiUrl = 'http://localhost:8080/compras';
  
  constructor(private http: HttpClient) { }

  /**
   * Crea un nueva compra realizando una petición POST a la API
   * @param purchaseRequest - DTO con los valores de Compra a agregar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  createPurchase(purchaseRequest: PurchaseRequestDTO): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}/crear`, purchaseRequest);
  }

  /**
   * Obtiene cantidad total de compras realizadas haciendo una petición GET a la API
   * @returns - Observable emite un valor numérico
   */
  getTotalPurchase(): Observable<number>{
    return this.http.get<number>(`${this.apiUrl}/total_compras`);
  }

  getAllPurchases(): Observable<PurchaseRequestDTO[]>{
    return this.http.get<PurchaseRequestDTO[]>(`${this.apiUrl}/lista-compras`);
  }

  getDetailPurchase(idPurchase: number): Observable<DetailPurchaseRequestDTO[]>{
    return this.http.get<DetailPurchaseRequestDTO[]>(`${this.apiUrl}/detalles`, {
      params: {idPurchase: idPurchase.toString()}
    });
  }
}
