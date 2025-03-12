import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/Entity/user';
import { AuthService } from './auth.service';
import { UserDTO } from '../../models/DTO/user-dto';
import { UpdateUserDTO } from '../../models/DTO/update-user-dto';

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
    return this.httpClient.get<UserDTO[]>(this.apiUrl, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Agregar un  nuevo usuario 
   * @param user - usuario a agregar
   * @returns Observable que emite un mensaje de confirmación
   */
  addUser(user: User): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/nuevo-usuario`, user);
  }

  /**
   * Actualiza los datos de un usuario existente
   * @param user - usuario con la nueva información
   * @returns - Observable que emite un mensaje de confirmación
   */
  updateUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/editar-usuario`, user);
  }

  /**
   * Elimina (desactiva) un usuario
   * @param user - usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  deleteUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar-usuario`, user);
  }

  /**
   * Activa un usuario 
   * @param user - usuario seleccionado
   * @returns - Observable que emite un mensaje de confirmación
   */
  activeUser(user: User): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar-usuario`, user);
  }

  getUserData(): Observable<UserDTO>{
    return this.httpClient.get<UserDTO>(`${this.apiUrl}/datos`, {headers: this.auth.getAuthHeaders()});
  }

  updateDataUser(userDTO: UpdateUserDTO): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar-usuario`, 
      userDTO, {headers: this.auth.getAuthHeaders()});
  }
}
