import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AlertModel } from '../../models/Entity/alert-model';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private apiUrl = 'http://localhost:8080/data';

  constructor(private httpClient: HttpClient) { }

  getAlerts(): Observable<AlertModel[]>{
    return this.httpClient.get<AlertModel[]>(this.apiUrl);
  }

  handleStatusAlert(alert: AlertModel, action: number): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, alert,{params:{action: action.toString()}});
  }
}
