import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class UserPermissionsService {

  constructor(private auth: AuthService) { }

  canViewAlert(): boolean {
    return this.auth.getRole() !== Role.SELLER && this.auth.getRole() !== Role.GUEST;;
  }
}
