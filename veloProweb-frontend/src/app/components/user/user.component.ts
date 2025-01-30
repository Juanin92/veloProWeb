import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/User/user.service';
import { User } from '../../models/Entity/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit{

  userList: User[] = [];
  userSelected: User | null = null;
  addUserButton: boolean = false;

  constructor(private userService: UserService){}

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void{
    this.userService.getListUsers().subscribe({
      next: (list) =>{
        this.userList = list;
      },
      error: (error) =>{
        console.log("Error, no se encontró una registro de usuarios");
      }
    });
  }

  createUser(): void{
    this.addUserButton = true;
  }
}
