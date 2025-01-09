import { Purchase } from "../models/Entity/Purchase/purchase";

export class PurchaseValidator {
    static validateForm(purchase: Purchase): boolean{
        return this.isFieldValid(purchase, 'document') &&
            this.isFieldValid(purchase, 'date') && 
            this.isFieldValid(purchase, 'supplier') && 
            this.isFieldValid(purchase, 'purchaseTotal');
    }

    static isFieldValid(purchase: Purchase, fieldName: keyof Purchase): boolean{
        const value = purchase[fieldName];
        if (fieldName === 'document') {
            return value !== null && typeof value === 'string' && value.trim().length > 0;
        }
        if (fieldName === 'date') {
            return value !== null && typeof value === 'string' && value.trim().length > 0 ;
        }
        if (fieldName === 'supplier') {
            return value !== null;
        }
        if (fieldName === 'purchaseTotal') {
            return value !== null && typeof value === 'number' && value !== 0;
        }
        return true;
    }

    static validateTotals(purchase: Purchase, total: number): boolean{
        return purchase.purchaseTotal !== total;
    }
}
