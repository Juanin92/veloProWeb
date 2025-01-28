import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Kardex } from '../../models/Entity/kardex';

@Injectable({
  providedIn: 'root'
})
export class KardexServiceService {

  private apiUrl = 'http://localhost:8080/kardex';

  constructor(private http: HttpClient) { }

  getAllKardex(): Observable<Kardex[]> {
    return this.http.get<Kardex[]>(this.apiUrl);
  }
}
