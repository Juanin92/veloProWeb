import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PurchaseRequestDTO } from '../../models/DTO/purchase-request-dto';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private apiUrl = 'http://localhost:8080/compras';
  
  constructor(private http: HttpClient) { }

  createPurchase(purchaseRequest: PurchaseRequestDTO): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}/crear`, purchaseRequest);
  }

  getTotalPurchase(): Observable<number>{
    return this.http.get<number>(`${this.apiUrl}/total_compras`);
  }
}
