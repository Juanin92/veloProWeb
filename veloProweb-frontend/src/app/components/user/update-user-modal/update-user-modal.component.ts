import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/User/user.service';
import { UserDTO } from '../../../models/DTO/user-dto';
import { NotificationService } from '../../../utils/notification-service.service';
import { UpdateUserDTO } from '../../../models/DTO/update-user-dto';
import { ModalService } from '../../../utils/modal.service';
import { UserValidator } from '../../../validation/user-validator';

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
  currentPassword: string = '';
  userDTO: UserDTO = this.initializeUser();
  updateUserDto: UpdateUserDTO | null = null;
  validator = UserValidator;

  constructor(
    private userService: UserService,
    private notification: NotificationService,
    public modal: ModalService) {}

  ngOnInit(): void {
    this.getData();
    this.modal.openModal();
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

  updateUser(userDTO: UserDTO): void{
    this.updateUserDto = this.initializeUpdateUser(userDTO);
    this.userService.updateDataUser(this.updateUserDto).subscribe({
      next:(response)=>{
        console.log('Usuario agregado exitosamente:', response);
        this.notification.showSuccessToast(response.message, 'top', 3000);
        this.resetModalUser();
        this.modal.closeModal();
      },error:(error)=>{
        const message = error.error?.message || error.error?.error || error?.error;
        console.error('Error al agregar el usuario:', message);
        this.notification.showErrorToast(`Error al agregar usuario \n${message}`, 'top', 5000);
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

  private initializeUpdateUser(user: UserDTO): UpdateUserDTO {
    return {
      username: user.username,
      email: user.email,
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmPassword: this.newPasswordConfirmed
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
