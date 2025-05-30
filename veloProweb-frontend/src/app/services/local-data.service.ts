import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LocalData } from '../models/Entity/data/local-data';
import { Observable } from 'rxjs';
import { AuthService } from './User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class LocalDataService {

  private apiUrl = 'http://localhost:8080/data';
      
    constructor(private httpClient: HttpClient, private auth: AuthService) { }
  
    getData(): Observable<LocalData>{
      return this.httpClient.get<LocalData>(this.apiUrl);
    }
    
    updateData(data: LocalData): Observable<{message: string}>{
      return this.httpClient.put<{message: string}>(this.apiUrl, data, {headers: this.auth.getAuthHeaders()});
    }
}
