import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from '../../models/Entity/message';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private apiUrl = 'http://localhost:8080/mensajes';
  
  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getMessages(userID: number): Observable<Message[]>{
    return this.httpClient.get<Message[]>(this.apiUrl,{params:{userId: userID.toString()}});
  }

  readMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, message, {headers: this.auth.getAuthHeaders()});
  }

  deleteMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar`, message, {headers: this.auth.getAuthHeaders()});
  }

  sendMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, message, {headers: this.auth.getAuthHeaders()});
  }
}
