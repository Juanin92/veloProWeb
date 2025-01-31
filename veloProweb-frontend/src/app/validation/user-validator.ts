import { User } from "../models/Entity/user";
import { Role } from "../models/enum/role";

export class UserValidator {
    static validateForm(selectedUser: User): boolean{
            return this.isFieldValid(selectedUser, 'name') &&
                this.isFieldValid(selectedUser, 'name') &&
                this.isFieldValid(selectedUser, 'rut') &&
                this.isFieldValid(selectedUser, 'password') &&
                this.isFieldValid(selectedUser, 'email') && 
                this.isFieldValid(selectedUser, 'role');
        }
    
        static isFieldValid(user: User, fieldName: keyof User): boolean{
            const value = user[fieldName];
            if (fieldName === 'name') {
                return value !== null && typeof value === 'string' && value.trim().length > 3 && /^[a-zA-Z ]+$/.test(value.trim());
            }
            if (fieldName === 'surname') {
                return value !== null && typeof value === 'string' && value.trim().length > 3 && /^[a-zA-Z ]+$/.test(value.trim());
            }
            if (fieldName === 'rut') {
                return value !== null && typeof value === 'string' && /^\d{7,8}-\d|[kK]$$/.test(value);
            }
            if (fieldName === 'email') {
                return !value || typeof value === 'string' && /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value);
            }
            if (fieldName === 'password') {
                return value !== null && typeof value === 'string' && value.trim().length >= 7;
            }
            if (fieldName === 'role') {
                return value !== null;
            }
            return true;
        }
}
