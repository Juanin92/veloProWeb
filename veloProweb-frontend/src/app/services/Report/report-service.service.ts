import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { DailySaleCountDTO } from '../../models/DTO/Report/daily-sale-count-dto';

@Injectable({
  providedIn: 'root'
})
export class ReportServiceService {

  private apiUrl = 'http://localhost:8080/reportes';

  constructor(private http: HttpClient) { }

  getDailySale(startDate: string, endDate: string): Observable<DailySaleCountDTO[] | {message: string}>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<DailySaleCountDTO[]>(`${this.apiUrl}/cantidad_ventas`, { params })
      .pipe(
        catchError((error) => {
          return throwError(() => new Error(error.error.message || 'Error al obtener los datos.'));
        })
      );
  }
}
