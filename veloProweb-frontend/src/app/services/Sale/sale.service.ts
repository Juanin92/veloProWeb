import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private apiUrl = 'http://localhost:8080/ventas';

  constructor(private http: HttpClient) { }

  getTotalSale(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}`);
  }
}
