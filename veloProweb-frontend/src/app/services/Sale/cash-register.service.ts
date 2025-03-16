import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
}
