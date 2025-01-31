import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CashRegister } from '../../models/Entity/cash-register';

@Injectable({
  providedIn: 'root'
})
export class CashRegisterService {

  private apiUrl = 'http://localhost:8080/caja';
  
    constructor(private http: HttpClient) { }

    getCashRegisters(): Observable<CashRegister[]>{
      return this.http.get<CashRegister[]>(this.apiUrl);
    }
}
