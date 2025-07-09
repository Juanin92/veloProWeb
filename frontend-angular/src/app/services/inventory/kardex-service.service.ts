import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../user/auth.service';
import { Kardex } from '../../models/entity/inventory/kardex';

@Injectable({
  providedIn: 'root'
})
export class KardexServiceService {

  private apiUrl = 'http://localhost:8080/kardex';

  constructor(private http: HttpClient, private auth: AuthService) { }

  getAllKardex(): Observable<Kardex[]> {
    return this.http.get<Kardex[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
  }
}
