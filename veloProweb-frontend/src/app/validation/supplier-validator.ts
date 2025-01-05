import { Supplier } from "../models/Entity/Purchase/supplier";

export class SupplierValidator {
    static validateForm(selectedSupplier: Supplier): boolean{
        return this.isFieldValid(selectedSupplier, 'name') &&
            this.isFieldValid(selectedSupplier, 'rut') &&
            this.isFieldValid(selectedSupplier, 'phone') &&
            this.isFieldValid(selectedSupplier, 'email');
    }

    static isFieldValid(supplier: Supplier, fieldName: keyof Supplier): boolean{
        const value = supplier[fieldName];
        if (fieldName === 'name') {
            return value !== null && typeof value === 'string' && value.trim().length > 2;
        }
        if (fieldName === 'rut') {
            return value !== null && typeof value === 'string' && /^\d{7,8}-\d|[kK]$$/.test(value);
        }
        if (fieldName === 'phone') {
            return value !== null && typeof value === 'string' && value.trim().length == 13;
        }
        if (fieldName === 'email') {
            return !value || typeof value === 'string' && /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value);
        }
        return true;
    }
}
