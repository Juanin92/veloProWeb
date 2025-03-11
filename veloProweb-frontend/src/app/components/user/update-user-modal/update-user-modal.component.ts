import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../../models/Entity/user';
import { Role } from '../../../models/enum/role';
import { UserService } from '../../../services/User/user.service';

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
  user: User = this.initializeUser();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.getData();
  }

  getData(){
    this.userService.getUserData().subscribe({
      next:(user)=>{
        this.user = user;
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

  private initializeUser(): User{
    return {
      id: 0,
      date: '',
      name: '',
      surname: '',
      username: '',
      rut: '',
      email: '',
      password: '',
      token: '',
      status: true,
      role: Role.GUEST
    }
  }
}
