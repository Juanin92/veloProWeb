import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Record } from '../../models/Entity/reporting/record';

@Injectable({
  providedIn: 'root'
})
export class RecordService {

  private apiUrl = 'http://localhost:8080/registro';
    
  constructor(private httpClient: HttpClient) { }

  getRecords(): Observable<Record[]>{
    return this.httpClient.get<Record[]>(this.apiUrl);
  }
}
