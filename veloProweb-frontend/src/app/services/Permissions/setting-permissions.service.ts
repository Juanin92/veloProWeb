import { Injectable } from '@angular/core';
import { AuthService } from '../user/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class SettingPermissionsService {

  constructor(private auth: AuthService) { }

  canViewSetting(): boolean {
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
  }
  canViewCashierMovements(): boolean {
    return this.auth.getRole() === Role.MASTER || this.auth.getRole() === Role.ADMIN;
  }
  canViewRegisters(): boolean {
    return this.auth.getRole() === Role.MASTER;
  }
  canViewTaskLayout(): boolean {
    return this.auth.getRole() === Role.MASTER || this.auth.getRole() === Role.ADMIN;
  }
  canViewDispatchLayout(): boolean{
    return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
  }
}
