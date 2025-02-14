import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from '../../models/Entity/message';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private apiUrl = 'http://localhost:8080/mensajes';
  
  constructor(private httpClient: HttpClient) { }

  getMessages(userID: number): Observable<Message[]>{
    return this.httpClient.get<Message[]>(this.apiUrl,{params:{userId: userID.toString()}});
  }

  readMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(this.apiUrl, message);
  }

  deleteMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar`, message);
  }

  sendMessage(message: Message): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, message);
  }
}
