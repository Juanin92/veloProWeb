import { Injectable } from '@angular/core';
import { UserResponse } from '../../models/entity/user/user-response';
import { UpdateUserForm } from '../../models/entity/user/update-user-form';
import { Role } from '../../models/enum/role';

@Injectable({
  providedIn: 'root'
})
export class UserHelperService {

  constructor() { }

  public initializeUpdateUser(user: UserResponse, currentPassword: string, 
    newPassword: string, newPasswordConfirmed: string): UpdateUserForm {
      return {
        username: user.username,
        email: user.email,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: newPasswordConfirmed
      }
    }
  
    public initializeUser(): UserResponse{
      return {
        name: '',
        surname: '',
        username: '',
        rut: '',
        email: '',
        status: true,
        role: Role.VOID,
        date: ''
      }
    }
}
