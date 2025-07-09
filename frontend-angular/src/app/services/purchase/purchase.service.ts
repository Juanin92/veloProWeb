import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../user/auth.service';
import { PurchaseRequest } from '../../models/entity/purchase/purchase-request';
import { PurchaseResponse } from '../../models/entity/purchase/purchase-response';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private apiUrl = 'http://localhost:8080/compras';
  
  constructor(private http: HttpClient, private auth: AuthService) { }

  /**
   * Crea un nueva compra realizando una petición POST a la API
   * @param purchaseRequest - DTO con los valores de Compra a agregar
   * @returns - Observable emite un mensaje de confirmación o error
   */
  createPurchase(purchaseRequest: PurchaseRequest): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}/crear`, purchaseRequest, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Obtiene cantidad total de compras realizadas haciendo una petición GET a la API
   * @returns - Observable emite un valor numérico
   */
  getTotalPurchase(): Observable<number>{
    return this.http.get<number>(`${this.apiUrl}/total_compras`);
  }

  getAllPurchases(): Observable<PurchaseResponse[]>{
    return this.http.get<PurchaseResponse[]>(`${this.apiUrl}/lista-compras`, {headers: this.auth.getAuthHeaders()});
  }
}
