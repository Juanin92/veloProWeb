import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LocalData } from '../models/Entity/local-data';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocalDataService {

  private apiUrl = 'http://localhost:8080/data';
      
    constructor(private httpClient: HttpClient) { }
  
    getData(): Observable<LocalData[]>{
      return this.httpClient.get<LocalData[]>(this.apiUrl);
    }
}
