import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {

  private apiUrl = 'http://localhost:8080/proveedores';

  constructor(private http: HttpClient) { }

  getSuppliers(): Observable<>{
    return this.http.get<>(this.apiUrl);
  }

  createSupplier(supplier:): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.apiUrl}`, supplier);
  }
}
