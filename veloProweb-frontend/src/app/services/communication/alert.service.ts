import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Alert } from '../../models/Entity/communication/alert';
import { AuthService } from '../User/auth.service';
import { AlertStatus } from '../../models/enum/alert-status';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private apiUrl = 'http://localhost:8080/alertas';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getAlerts(): Observable<Alert[]>{
    return this.httpClient.get<Alert[]>(this.apiUrl);
  }

  handleStatusAlert(alertId: number, action: AlertStatus): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, null,
      {params:{alertId: alertId.toString(), action: action}, 
      headers: this.auth.getAuthHeaders()}
    );
  }
}
