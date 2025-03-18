import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class PurchasePermissionsService {

  constructor(private auth: AuthService) { }

  canViewPurchase(): boolean{
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER;
  }

  canViewSupplier(): boolean{
    return this.auth.getRole() !== Role.GUEST && this.auth.getRole() !== Role.SELLER;
  }
}
