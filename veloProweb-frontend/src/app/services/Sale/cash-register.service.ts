import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { CashRegister } from '../../models/Entity/cash-register';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CashRegisterService {

  private apiUrl = 'http://localhost:8080/caja';
  
    constructor(private http: HttpClient, private auth: AuthService) { }

    getCashRegisters(): Observable<CashRegister[]>{
      return this.http.get<CashRegister[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
    }

    hasOpenRegisterOnDate(): Observable<boolean>{
      return this.http.get<{isOpen: boolean}>(`${this.apiUrl}/verificar-apertura`, {headers: this.auth.getAuthHeaders()})
      .pipe(map(response => response.isOpen));
    }

    addRegisterOpening(amount: number): Observable<{message: string}>{
      return this.http.post<{message: string}>(`${this.apiUrl}/apertura`, amount, {headers: this.auth.getAuthHeaders()});
    }

    addRegisterClosing(cashier: CashRegister): Observable<{message: string}>{
      return this.http.post<{message: string}>(`${this.apiUrl}/cierre`, cashier, {headers: this.auth.getAuthHeaders()});
    }
}
