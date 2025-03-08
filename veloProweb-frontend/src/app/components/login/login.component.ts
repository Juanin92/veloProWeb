import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HomeComponent } from '../Home/home.component';
import { LoginRequest } from '../../models/DTO/login-request';
import { AuthService } from '../../services/User/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../utils/notification-service.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  userLogin: LoginRequest = {
    username: '',
    password: ''
  };

  constructor(
    private router: Router,
    private authService: AuthService,
    private notification: NotificationService){}

    isLoginUser() {
      if (this.userLogin) {
          this.authService.login(this.userLogin).subscribe({
              next: (response) => {
                  this.authService.saveToken(response.token);
                  this.authService.saveRole(response.role);
                  this.router.navigate(['/main/home']);
              },
              error: (error) => {
                  console.error('login.component: login error', error);
                  this.notification.showWarning('Error!', 'Las credenciales no son correctas, intente de nuevo');
              }
          });
      }
  }
}
