import { Injectable } from '@angular/core';
import { AuthService } from '../User/auth.service';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class DispatchPermissionsService {

  constructor(private auth: AuthService) { }
  
    canViewDispatch(): boolean{
      return this.auth.getRole() !== Role.WAREHOUSE;
    }

    canViewDispatchLayout(): boolean{
      return this.auth.getRole() !== Role.WAREHOUSE && this.auth.getRole() !== Role.GUEST;
    }
  
    canHandleDispatch(): boolean{
      return this.auth.getRole() !== Role.GUEST;
    }
}
