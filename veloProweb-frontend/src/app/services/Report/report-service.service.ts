import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { ReportDTO } from '../../models/DTO/Report/report-dto';

@Injectable({
  providedIn: 'root'
})
export class ReportServiceService {

  private apiUrl = 'http://localhost:8080/reportes';

  constructor(private http: HttpClient) { }

  getDailySale(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/cantidad_ventas`, { params })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getTotalSaleDaily(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/monto_ventas`, { params })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getAverageTotalSaleDaily(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/promedio_ventas`, { params })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getEarningSale(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/ganancias_ventas`, { params })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }
}
