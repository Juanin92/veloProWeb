import { User } from "../models/Entity/user";

export class UserValidator {
    static validateForm(selectedUser: User): boolean {
        return this.isFieldValid(selectedUser, 'name') &&
            this.isFieldValid(selectedUser, 'surname') &&
            this.isFieldValid(selectedUser, 'username') &&
            this.isFieldValid(selectedUser, 'rut') &&
            this.isFieldValid(selectedUser, 'password') &&
            this.isFieldValid(selectedUser, 'email') &&
            this.isFieldValid(selectedUser, 'role');
    }

    static isFieldValid(user: User, fieldName: keyof User): boolean {
        const value = user[fieldName];
        if (fieldName === 'name') {
            return value !== null && typeof value === 'string' && value.trim().length > 3 && /^[a-zA-Z ]+$/.test(value.trim());
        }
        if (fieldName === 'surname') {
            return value !== null && typeof value === 'string' && value.trim().length > 3 && /^[a-zA-Z ]+$/.test(value.trim());
        }
        if (fieldName === 'username') {
            return value !== null && typeof value === 'string' && value.trim().length > 3;
        }
        if (fieldName === 'rut') {
            return value !== null && typeof value === 'string' && /^\d{7,8}-\d|[kK]$$/.test(value);
        }
        if (fieldName === 'email') {
            return value !== null && typeof value === 'string' && /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value);
        }
        if (fieldName === 'password') {
            return value !== null && typeof value === 'string' && value.trim().length >= 7;
        }
        if (fieldName === 'role') {
            return value !== null;
        }
        return true;
    }

    static validateUsername(username: string): boolean{
        return username !== null && typeof username === 'string' && username.trim().length > 3;
    }
    static validateEmail(email: string): boolean{
        return email !== null && typeof email === 'string' && /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);
    }
    static validatePassword(password: string): boolean{
        return password !== null && typeof password === 'string' && password.trim().length > 3;
    }
}
