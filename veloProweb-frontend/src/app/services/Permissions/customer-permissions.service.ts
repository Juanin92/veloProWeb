import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class CustomerPermissionsService {

  constructor(private auth: AuthService) { }

  canViewUser(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE;
  }

  canAddUser(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
  }

  canUpdateUser(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
  }

  canChangeInputValue(): boolean{
    return this.auth.getRole() === Role.SELLER;
  }

  canDeleteUser(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST && 
    this.auth.getRole() !== Role.SELLER;
  }
}
