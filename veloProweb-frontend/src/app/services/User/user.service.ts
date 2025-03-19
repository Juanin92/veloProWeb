import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { UserDTO } from '../../models/DTO/user-dto';
import { UpdateUserDTO } from '../../models/DTO/update-user-dto';
import { AuthRequestDTO } from '../../models/DTO/auth-request-dto';

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
  getListUsers(): Observable<UserDTO[]>{
    return this.httpClient.get<UserDTO[]>(this.apiUrl);
  }

  /**
   * Agregar un  nuevo usuario 
   * @param user - usuario a agregar
   * @returns Observable que emite un mensaje de confirmación
   */
  addUser(user: UserDTO): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/nuevo-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Actualiza los datos de un usuario existente
   * @param user - usuario con la nueva información
   * @returns - Observable que emite un mensaje de confirmación
   */
  updateUserByAdmin(user: UserDTO): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/administrar/editar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Elimina (desactiva) un usuario
   * @param username - nombre de usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  deleteUser(user: UserDTO): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Activa un usuario 
   * @param user - usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  activeUser(user: UserDTO): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar-usuario`, 
      user, {headers: this.auth.getAuthHeaders()});
  }

  getUserData(): Observable<UserDTO>{
    return this.httpClient.get<UserDTO>(`${this.apiUrl}/datos`, {headers: this.auth.getAuthHeaders()});
  }

  updateDataUser(userDTO: UpdateUserDTO): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar-usuario`, 
      userDTO, {headers: this.auth.getAuthHeaders()});
  }
}
