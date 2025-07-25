import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginRequest } from '../../models/entity/user/login-request';
import { Observable } from 'rxjs';
import { AuthRequest } from '../../models/entity/user/auth-request';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private httpClient: HttpClient) { }

  getEncryptionKey(): Observable<string> {
    return this.httpClient.get(`${this.apiUrl}/encriptado`, { responseType: 'text' });
  }

  login(loginRequest: LoginRequest): Observable<{ token: string, role: string }> {
    return this.httpClient.post<{ token: string, role: string }>(`${this.apiUrl}/login`, loginRequest);
  }

  loginWithCode(loginRequest: LoginRequest): Observable<{token: string, role: string}>{
    return this.httpClient.post<{ token: string, role: string }>(`${this.apiUrl}/login/code`, loginRequest);
  }

  logout(): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(`${this.apiUrl}/logout`, {}, { headers: this.getAuthHeaders() });
  }

  getAuthAccess(auth: AuthRequest): Observable<boolean>{
    return this.httpClient.post<boolean>(`${this.apiUrl}/verificar`, auth, { headers: this.getAuthHeaders() });;
  }

  sendEmailCode(loginRequest: LoginRequest): Observable<{action: boolean,message: string}>{
    return this.httpClient.post<{action: boolean, message: string}>(`${this.apiUrl}/olvide-codigo`, loginRequest);
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }
  saveRole(role: string): void {
    localStorage.setItem('role', role);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
  getRole(): string | null {
    return localStorage.getItem('role');
  }

  removeToken(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
