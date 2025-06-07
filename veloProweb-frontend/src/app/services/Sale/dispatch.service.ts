import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dispatch } from '../../models/entity/sale/dispatch';
import { AuthService } from '../user/auth.service';
import { DispatchRequest } from '../../models/entity/sale/dispatch-request';
import { DispatchStatus } from '../../models/enum/dispatch-status';

@Injectable({
  providedIn: 'root'
})
export class DispatchService {

  private apiUrl = 'http://localhost:8080/despachos';

  constructor(private http: HttpClient, private auth: AuthService) { }

  getDispatches(): Observable<Dispatch[]>{
    return this.http.get<Dispatch[]>(this.apiUrl);
  };

  createDispatch(dispatch: DispatchRequest): Observable<{message: string}>{
    return this.http.post<{message: string}>(this.apiUrl, dispatch, {headers: this.auth.getAuthHeaders()});
  }

  handleStatusDispatch(dispatchID: number, action: DispatchStatus): Observable<{message: string}>{
    return this.http.put<{message: string}>(this.apiUrl, {}, {
      params:{
        dispatchID: dispatchID.toString(),
        action: action.toString()},
        headers: this.auth.getAuthHeaders()
    });
  }
}
