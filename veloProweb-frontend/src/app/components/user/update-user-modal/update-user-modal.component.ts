import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/User/user.service';
import { UserDTO } from '../../../models/DTO/user-dto';

@Component({
  selector: 'app-update-user-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-user-modal.component.html',
  styleUrl: './update-user-modal.component.css'
})
export class UpdateUserModalComponent implements OnInit{

  @ViewChild('changePasswordCheckbox') changePasswordCheckbox!: ElementRef;
  changePassword: boolean = false;
  newPassword: string = '';
  newPasswordConfirmed: string = '';
  userDTO: UserDTO = this.initializeUser();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.getData();
  }

  getData(){
    this.userService.getUserData().subscribe({
      next:(user)=>{
        this.userDTO = user;
      }, error: (error) =>{
        console.log('Error: ', error?.error);
      }
    });
  }

  isChangePassword(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.changePassword = input.checked;
    this.newPassword = '';
    this.newPasswordConfirmed = '';

  }

  resetModalUser(): void {
    this.changePassword = false;
    this.newPassword = '';
    this.newPasswordConfirmed = '';
    if (this.changePasswordCheckbox) {
      this.changePasswordCheckbox.nativeElement.checked = false;
    }
    this.initializeUser();
  }

  changeNameRole(role: string | null): string{
    switch(role){
      case 'MASTER': return 'Maestro';
      case 'ADMIN': return 'Administrador';
      case 'GUEST': return 'Invitado';
      case 'WAREHOUSE': return 'Logística';
      case 'SELLER': return 'Vendedor';
      default: return 'Sin Rol';
    }
  }

  private initializeUser(): UserDTO{
    return {
      name: '',
      surname: '',
      username: '',
      rut: '',
      email: '',
      token: '',
      status: true,
      role: null
    }
  }
}
