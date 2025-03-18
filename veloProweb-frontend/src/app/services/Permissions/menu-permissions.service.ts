import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class MenuPermissionsService {

  constructor(private auth: AuthService) { }

  canViewSale(): boolean {
    return this.auth.getRole() !== Role.WAREHOUSE;
  }
  canViewCustomer(): boolean {
    return this.auth.getRole() !== Role.WAREHOUSE;
  }
  canViewReport(): boolean {
    return this.auth.getRole() !== Role.GUEST;
  }
  canViewSetting(): boolean {
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.WAREHOUSE;
  }
  canViewUser(): boolean {
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER && 
    this.auth.getRole() !== Role.WAREHOUSE;
  }
}
