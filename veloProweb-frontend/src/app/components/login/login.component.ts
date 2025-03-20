import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HomeComponent } from '../Home/home.component';
import { LoginRequest } from '../../models/DTO/login-request';
import { AuthService } from '../../services/User/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../utils/notification-service.service';
import { EncryptionService } from '../../security/encryption.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  encryptionKey: string = '';
  sendCode: boolean = false;
  userLogin: LoginRequest = {
    username: '',
    password: ''
  };

  constructor(
    private router: Router,
    private authService: AuthService,
    private encryptionService: EncryptionService,
    private notification: NotificationService) { }

  ngOnInit(): void {
    this.getEncryptedKey();
  }

  getEncryptedKey(): void{
    this.authService.getEncryptionKey().subscribe({
      next: (key) => { this.encryptionKey = key;},
      error: (error) => {
        const message = error.error?.error || error?.error;
        console.log('Error: ', message);
      }
    });
  }

  isLoginUser() {
    if (this.userLogin && this.encryptionKey) {
      const encryptedUser: LoginRequest = {
        username: this.userLogin.username,
        password: this.encryptionService.encryptPassword(this.userLogin.password, this.encryptionKey)
      }
      this.authService.login(encryptedUser).subscribe({
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
