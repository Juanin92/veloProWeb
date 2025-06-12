import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRequest } from '../../models/DTO/login-request';
import { AuthService } from '../../services/user/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../utils/notification-service.service';
import { EncryptionService } from '../../security/encryption.service';
import { ErrorMessageService } from '../../utils/error-message.service';

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
    private notification: NotificationService,
    private errorMessage: ErrorMessageService) { }

  ngOnInit(): void {
    this.getEncryptedKey();
    this.resetCache();
  }

  getEncryptedKey(): void{
    this.authService.getEncryptionKey().subscribe((key) => { 
      this.encryptionKey = key;
    });
  }

  isLoginUser() {
    if (this.userLogin && this.encryptionKey) {
      const encryptedUser: LoginRequest = {
        username: this.userLogin.username,
        password: this.encryptionService.encryptPassword(this.userLogin.password, this.encryptionKey)
      }
      if(this.sendCode){
        this.loginWithCode(encryptedUser);
      }else{
        this.normalLogin(encryptedUser);
      }
    }
  }

  normalLogin(encryptedUser: LoginRequest): void{
    this.authService.login(encryptedUser).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.authService.saveRole(response.role);
        this.router.navigate(['/main/home']);
      },error: () => {
        this.notification.showWarning('Error!', 'Las credenciales no son correctas, intente de nuevo');
      }
    });
  }

  loginWithCode(encryptedUser: LoginRequest): void{
    this.authService.loginWithCode(encryptedUser).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.authService.saveRole(response.role);
        this.router.navigate(['/main/home']);
      },error: () => {
        this.notification.showWarning('Error!', 'Las credenciales no son correctas, intente de nuevo');
      }
    });
  }

  sendEmail(): void{
    if (this.userLogin && this.userLogin.username) {
      this.authService.sendEmailCode(this.userLogin).subscribe({
        next:(response)=>{
          this.sendCode = response.action;
          this.notification.showSuccessToast(response.message, 'top', 3000);
          console.log('OK: ', response.message);
        }, error: (error)=>{
          this.sendCode = error.action;
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  resetCache(): void{
    sessionStorage.clear();
    localStorage.clear();
  }
}
