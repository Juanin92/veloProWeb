import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/User/user.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { UserValidator } from '../../../validation/user-validator';
import { UserPermissionsService } from '../../../services/Permissions/user-permissions.service';
import { UserResponse } from '../../../models/Entity/user/user-response';
import { UserMapperService } from '../../../mapper/user-mapper.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { UserHelperService } from '../../../services/User/user-helper.service';

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
  user: UserResponse;
  validator = UserValidator;

  constructor(
    private userService: UserService,
    private mapper: UserMapperService,
    private helper: UserHelperService,
    private errorMessage: ErrorMessageService,
    protected permission: UserPermissionsService,
    private notification: NotificationService,
    public modal: ModalService) {
      this.user = this.helper.initializeUser();
    }

  ngOnInit(): void {
    this.loadUserInformation();
    this.modal.openModal();
  }

  loadUserInformation(){
    this.userService.getUserData().subscribe({
      next:(user)=>{
        this.user = user;
      }
    });
  }

  updateUserData(user: UserResponse): void{
    const updateUser = this.mapper.mapToUserProfile(user, this.currentPassword, this.newPassword, this.newPasswordConfirmed);
    this.userService.updateDataUser(updateUser).subscribe({
      next:(response)=>{
        this.notification.showSuccessToast(response.message, 'top', 3000);
        this.resetModalUser();
        this.modal.closeModal();
      },error:(error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast(`Error: \n${message}`, 'top', 5000);
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
    this.loadUserInformation();
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
}
