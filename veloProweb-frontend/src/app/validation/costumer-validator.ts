import { Costumer } from "../models/Costumer/costumer.model";

export class CostumerValidator {
    static validateForm(selectedCostumer: Costumer): boolean {
        return this.isFieldValid(selectedCostumer, 'name') &&
            this.isFieldValid(selectedCostumer, 'surname') &&
            this.isFieldValid(selectedCostumer, 'phone') &&
            this.isFieldValid(selectedCostumer, 'email');
    }

    static isFieldValid(costumer: Costumer, fieldName: keyof Costumer): boolean {
        const value = costumer[fieldName];
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
