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

  /**
   * Obtiene una lista de todas las unidades de medidas desde el endpoint
   * @returns observable que emite una lista de unidades de medidas
   */
  getUnits(): Observable<UnitProductModel[]> {
    return this.httpClient.get<UnitProductModel[]>(this.apiUrl);
  }

  /**
   * Crea una nueva Unidad de medida
   * @param unit - Unidad de medida para agregar
   * @returns - Observable que emite un mensaje de confirmación o error
   */
  createUnit(unit: UnitProductModel): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(`${this.apiUrl}`, unit);
  }
}
