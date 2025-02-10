import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/User/user.service';
import { User } from '../../models/Entity/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserValidator } from '../../validation/user-validator';
import { Role } from '../../models/enum/role';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit{

  userList: User[] = [];
  addUserButton: boolean = false;
  showForm: boolean = false;
  existingMasterUser: boolean = true;
  validator = UserValidator;
  roles: string[] = Object.values(Role);
  user: User;
  selectedUser: User | null = null;
  touchedFields: Record<string, boolean> = {};

  roleLabels: { [key: string]: string } = {
    [Role.MASTER]: 'Maestro',
    [Role.ADMIN]: 'Administrador',
    [Role.GUEST]: 'Invitado',
    [Role.WAREHOUSE]: 'Bodega',
    [Role.SELLER]: 'Vendedor'
  };

  constructor(private userService: UserService){
    this.user = this.initializeUser();
  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void{
    this.userService.getListUsers().subscribe({
      next: (list) =>{
        this.userList = list;
        this.existingMasterUser = !this.userList.some(user => user.role === Role.MASTER);
      },
      error: (error) =>{
        console.log("Error, no se encontró una registro de usuarios");
      }
    });
  }

  getSelectedUser(selectedUser: User): void{
    this.user = selectedUser;
    this.showForm = true;
    this.addUserButton = false;
  }

  resetForms(): void{
    this.addUserButton = false;
    this.showForm =  false;
    this.initializeUser();
    this.touchedFields = {};
  }

  initializeUser(): User {
      return this.user = {
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
        role: null
      };
  }
}
