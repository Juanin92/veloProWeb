import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UnitProduct } from '../../models/entity/product/unit-product';
import { Observable } from 'rxjs';
import { AuthService } from '../user/auth.service';

@Injectable({
  providedIn: 'root',
})
export class UnitService {
  private apiUrl = 'http://localhost:8080/unidad';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene una lista de todas las unidades de medidas desde el endpoint
   * @returns observable que emite una lista de unidades de medidas
   */
  getUnits(): Observable<UnitProduct[]> {
    return this.httpClient.get<UnitProduct[]>(this.apiUrl);
  }

  /**
   * Crea una nueva Unidad de medida
   * @param unit - Unidad de medida para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createUnit(unit: UnitProduct): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(`${this.apiUrl}`, unit, {
      headers: this.auth.getAuthHeaders(),
    });
  }
}
