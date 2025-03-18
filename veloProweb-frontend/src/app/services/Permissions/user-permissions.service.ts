import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class UserPermissionsService {

  constructor(private auth: AuthService) { }

  canViewUser(): boolean {
    return this.auth.getRole() === Role.MASTER && this.auth.getRole() === Role.ADMIN;
  }
}
