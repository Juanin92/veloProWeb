import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { UserDTO } from '../../models/DTO/user-dto';
import { UpdateUserForm } from '../../models/Entity/user/update-user-form';
import { UserResponse } from '../../models/Entity/user/user-response';
import { UserForm } from '../../models/Entity/user/user-form';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/usuario';
  
  constructor(private httpClient: HttpClient, private auth: AuthService) { }

  /**
   * Obtiene una lista de todos los usuarios desde el endpoint
   * @returns observable que emite una lista de usuarios
   */
  getListUsers(): Observable<UserResponse[]>{
    return this.httpClient.get<UserResponse[]>(this.apiUrl);
  }

  /**
   * Agregar un  nuevo usuario 
   * @param user - usuario a agregar
   * @returns Observable que emite un mensaje de confirmación
   */
  addUser(user: UserForm): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/nuevo-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Actualiza los datos de un usuario existente
   * @param user - usuario con la nueva información
   * @returns - Observable que emite un mensaje de confirmación
   */
  updateUserByAdmin(user: UserForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/administrar/editar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Elimina (desactiva) un usuario
   * @param username - nombre de usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  deleteUser(user: UserForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Activa un usuario 
   * @param user - usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  activeUser(user: UserForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  getUserData(): Observable<UserResponse>{
    return this.httpClient.get<UserResponse>(`${this.apiUrl}/datos`, {headers: this.auth.getAuthHeaders()});
  }

  updateDataUser(user: UpdateUserForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }
}
