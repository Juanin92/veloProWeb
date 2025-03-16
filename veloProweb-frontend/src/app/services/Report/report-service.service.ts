import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { ReportDTO } from '../../models/DTO/Report/report-dto';
import { ProductReportDTO } from '../../models/DTO/Report/product-report-dto';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ReportServiceService {

  private apiUrl = 'http://localhost:8080/reportes';

  constructor(private http: HttpClient, private auth: AuthService) { }

  /**
   * Obtiene la cantidad de venta realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getDailySale(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/cantidad_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  /**
   * Obtiene el monto total de venta realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getTotalSaleDaily(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/monto_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  /**
   * Obtiene el promedio de venta realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getAverageTotalSaleDaily(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/promedio_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  /**
   * Obtiene la ganancia de venta realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getEarningSale(startDate: string, endDate: string): Observable<ReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ReportDTO[]>(`${this.apiUrl}/ganancias_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  /**
   * Obtiene los productos más vendidos realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getMostProductSale(startDate: string, endDate: string): Observable<ProductReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ProductReportDTO[]>(`${this.apiUrl}/producto_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  /**
   * Obtiene las categorías más vendidas realizando una petición GET a la API
   * @param startDate - Rango de fecha inicial
   * @param endDate - Rango de fecha final
   * @returns -  Observable emite una lista con objeto
   */
  getMostCategorySale(startDate: string, endDate: string): Observable<ProductReportDTO[]>{
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ProductReportDTO[]>(`${this.apiUrl}/categoria_ventas`, { params, headers: this.auth.getAuthHeaders() })
      .pipe(
        catchError((error) => {
          const errorMessage = error.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }
}
