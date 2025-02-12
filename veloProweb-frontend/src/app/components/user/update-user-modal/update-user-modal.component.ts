import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../../models/Entity/user';
import { Role } from '../../../models/enum/role';

@Component({
  selector: 'app-update-user-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-user-modal.component.html',
  styleUrl: './update-user-modal.component.css'
})
export class UpdateUserModalComponent {

  @ViewChild('changePasswordCheckbox') changePasswordCheckbox!: ElementRef;
  changePassword: boolean = false;
  newPassword: string = '';
  newPasswordConfirmed: string = '';
  user: User = {
    id:1,
    date: '10-02-2023',
    name: 'Juan Ignacio',
    surname: 'Claveria Cordero',
    username: 'juano',
    rut: '18212716-8',
    email: 'juano@gmial.com',
    password: 'juano1234',
    token: '',
    status: true,
    role: Role.MASTER
  };

  constructor(){}

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
}
}
