import { CustomerForm } from "../models/entity/customer/customer-form"

export class CustomerValidator {

    static validateForm(selectedCustomer: CustomerForm): boolean {
        return this.isFieldValid(selectedCustomer, 'name') &&
            this.isFieldValid(selectedCustomer, 'surname') &&
            this.isFieldValid(selectedCustomer, 'phone') &&
            this.isFieldValid(selectedCustomer, 'email');
    }

    static isFieldValid(customer: CustomerForm, fieldName: keyof CustomerForm): boolean {
        const value = customer[fieldName];
        if (fieldName === 'email') {
            return !value || typeof value === 'string' && /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value);
        }
        if (typeof value !== 'string' || value.trim().length < 3) {
            return false;
        }
        if (fieldName === 'phone' && value.trim().length < 13) {
            return false;
        }
        if (fieldName === 'name' || fieldName === 'surname') {
            return /^[a-zA-Z ]+$/.test(value.trim());
        }
        return true;
    }
}
