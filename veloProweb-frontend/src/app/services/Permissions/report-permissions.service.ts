import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class ReportPermissionsService {

  constructor(private auth: AuthService) { }
    
  canViewReport(): boolean{
    return this.auth.getRole() !== Role.SELLER && this.auth.getRole() !== Role.GUEST;
  }
  canSaleReport(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE;
  }
  canViewKardex(): boolean{
    return this.auth.getRole() !== Role.SELLER && this.auth.getRole() !== Role.GUEST;
  }
  canViewPurchaseReport(): boolean{
    return this.auth.getRole() !== Role.SELLER && this.auth.getRole() !== Role.GUEST;
  }
  canViewSaleReport(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
  }
}
