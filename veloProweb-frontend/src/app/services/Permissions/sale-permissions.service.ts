import { Injectable } from '@angular/core';
import { AuthService } from '../user/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class SalePermissionsService {

  constructor(private auth: AuthService) { }

  isSaleViewAllowed(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE;
  }

  isLoanPaymentAllowed(): boolean{
    return this.auth.getRole() !== Role.GUEST;
  }
}
