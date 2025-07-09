import { Injectable } from '@angular/core';
import { AuthService } from '../user/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class UserPermissionsService {

  constructor(private auth: AuthService) { }

  canViewUser(): boolean {
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.SELLER && 
    this.auth.getRole() !== Role.GUEST;
  }
  canViewAlert(): boolean {
    return this.auth.getRole() !== Role.SELLER && this.auth.getRole() !== Role.GUEST;;
  }
}
