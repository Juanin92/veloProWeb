import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HomeComponent } from '../Home/home.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private router: Router){}

  isLoginUser(){
    this.router.navigate(['/home']);
  }
}
