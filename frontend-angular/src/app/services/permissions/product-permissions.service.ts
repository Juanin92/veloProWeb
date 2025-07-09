import { Injectable } from '@angular/core';
import { AuthService } from '../user/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class ProductPermissionsService {

  constructor(private auth: AuthService) { }

  canAddProduct(): boolean{
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER;
  }
  canUpdateProduct(): boolean{
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER;
  }

  canAddCategories(): boolean{
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER;
  }

}
