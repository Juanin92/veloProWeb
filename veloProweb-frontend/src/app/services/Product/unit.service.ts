import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UnitProductModel } from '../../models/Entity/Product/unit-product';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UnitService {
  private apiUrl = 'http://localhost:8080/unidad';

  constructor(private httpClient: HttpClient) { }

  getUnits(): Observable<UnitProductModel[]> {
    return this.httpClient.get<UnitProductModel[]>(this.apiUrl);
  }

  createUnit(unit: UnitProductModel): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(`${this.apiUrl}/`, unit);
  }
}
