import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AlertModel } from '../../models/Entity/alert-model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private apiUrl = 'http://localhost:8080/alertas';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getAlerts(): Observable<AlertModel[]>{
    return this.httpClient.get<AlertModel[]>(this.apiUrl);
  }

  handleStatusAlert(alert: AlertModel, action: number): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, alert,
      {params:{action: action.toString()}, headers: this.auth.getAuthHeaders()});
  }
}
