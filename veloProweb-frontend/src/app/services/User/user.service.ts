import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/Entity/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/usuario';
  
  constructor(private httpClient: HttpClient) { }

  getListUsers(): Observable<User[]>{
    return this.httpClient.get<User[]>(this.apiUrl);
  }

  addUser(user: User): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/nuevo-usuario`, user);
  }

  updateUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/editar-usuario`, user);
  }

  deleteUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar-usuario`, user);
  }

  activeUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar-usuario`, user);
  }
}
