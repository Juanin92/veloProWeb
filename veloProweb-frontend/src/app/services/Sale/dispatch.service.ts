import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dispatch } from '../../models/Entity/Sale/dispatch';
import { DetailSaleRequestDTO } from '../../models/DTO/detail-sale-request-dto';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class DispatchService {

  private apiUrl = 'http://localhost:8080/despachos';

  constructor(private http: HttpClient, private auth: AuthService) { }

  getDispatches(): Observable<Dispatch[]>{
    return this.http.get<Dispatch[]>(this.apiUrl);
  };

  createDispatch(dispatch: Dispatch): Observable<{message: string}>{
    return this.http.post<{message: string}>(this.apiUrl, dispatch, {headers: this.auth.getAuthHeaders()});
  }

  handleStatusDispatch(dispatchID: number, action: number): Observable<{message: string}>{
    return this.http.put<{message: string}>(this.apiUrl, {}, {
      params:{
        dispatchID: dispatchID.toString(),
        action: action.toString()},
        headers: this.auth.getAuthHeaders()
    });
  }

  getDetailSale(idDispatch: number): Observable<DetailSaleRequestDTO[]>{
    return this.http.get<DetailSaleRequestDTO[]>(`${this.apiUrl}/detalles`, {
      params: {idDispatch: idDispatch.toString()}
    });
  }
}
