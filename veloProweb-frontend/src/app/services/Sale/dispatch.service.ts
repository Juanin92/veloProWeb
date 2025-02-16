import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dispatch } from '../../models/Entity/Sale/dispatch';

@Injectable({
  providedIn: 'root'
})
export class DispatchService {

  private apiUrl = 'http://localhost:8080/despachos';

  constructor(private http: HttpClient) { }

  getDispatches(): Observable<Dispatch[]>{
    return this.http.get<Dispatch[]>(this.apiUrl);
  };

  createDispatch(dispatch: Dispatch): Observable<{message: string}>{
    return this.http.post<{message: string}>(this.apiUrl, dispatch);
  }

  handleStatusDispatch(dispatchID: number, action: number): Observable<{message: string}>{
    return this.http.put<{message: string}>(this.apiUrl, {}, {
      params:{
        dispatchID: dispatchID.toString(),
        action: action.toString()}
    });
  }
}
