import { Injectable } from '@angular/core';
import { UserResponse } from '../models/entity/user/user-response';
import { UserForm } from '../models/entity/user/user-form';
import { UpdateUserForm } from '../models/entity/user/update-user-form';

@Injectable({
  providedIn: 'root'
})
export class UserMapperService {

  constructor() { }

  mapToUserForm(user: UserResponse): UserForm{
    return {
      name: user.name,
      surname: user.surname,
      username: user.username,
      rut: user.rut,
      email: user.email,
      role: user.role
    }
  }

  mapToUserProfile(user: UserResponse, currentPassword: string, 
    newPassword: string, newPasswordConfirmed: string): UpdateUserForm{
    return {
      username: user.username,
      email: user.email,
      currentPassword: currentPassword,
      newPassword: newPassword,
      confirmPassword: newPasswordConfirmed
    }
  }
}
