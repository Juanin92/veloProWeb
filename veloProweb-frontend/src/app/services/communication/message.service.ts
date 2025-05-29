import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from '../../models/Entity/communication/message';
import { AuthService } from '../User/auth.service';
import { MessageForm } from '../../models/Entity/communication/message-form';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private apiUrl = 'http://localhost:8080/mensajes';

  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  getMessagesByUser(): Observable<Message[]> {
    return this.httpClient.get<Message[]>(this.apiUrl, { headers: this.auth.getAuthHeaders() });
  }

  markMessageAsRead(messageId: number): Observable<{ message: string }> {
    return this.httpClient.put<{ message: string }>(this.apiUrl, null, {
      headers: this.auth.getAuthHeaders(),
      params: new HttpParams().set('id', messageId.toString())
    });
  }

  markMessageAsDeleted(messageId: number): Observable<{ message: string }> {
    return this.httpClient.put<{ message: string }>(`${this.apiUrl}/eliminar`, null, {
      headers: this.auth.getAuthHeaders(),
      params: new HttpParams().set('id', messageId.toString())
    });
  }

  sendMessage(message: MessageForm): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(this.apiUrl, message,
      { headers: this.auth.getAuthHeaders() });
  }
}
