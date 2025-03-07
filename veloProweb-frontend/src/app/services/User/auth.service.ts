import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginRequest } from '../../models/DTO/login-request';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';
    
  constructor(private httpClient: HttpClient) { }

  login(loginRequest: LoginRequest): Observable<{token: string, role: string}> {
    return this.httpClient.post<{token: string, role: string}>(`${this.apiUrl}/login`, loginRequest);
  }

  saveToken(token: string): void {
    sessionStorage.setItem('token', token);
  }
  saveRole(role: string): void {
    sessionStorage.setItem('role', role); 
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');
  }
  getRole(): string | null {
    return sessionStorage.getItem('role');
  }

  removeToken(): void {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('role');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
