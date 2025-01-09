import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private apiUrl = 'http://localhost:8080/compras';
  
  constructor(private http: HttpClient) { }

  getTotalPurchase(): Observable<number>{
    return this.http.get<number>(`${this.apiUrl}/total_compras`);
  }
}
