import { Product } from "../models/Entity/Product/product.model";

export class ProductValidator {
    static validateForm(selectedProduct: Product): boolean {
            return this.isFieldValid(selectedProduct, 'description') &&
                    this.isFieldValid(selectedProduct, 'brand') &&
                    this.isFieldValid(selectedProduct, 'category') &&
                    this.isFieldValid(selectedProduct, 'subcategoryProduct') &&
                    this.isFieldValid(selectedProduct, 'unit');
        }
    
        static isFieldValid(product: Product, fieldName: keyof Product): boolean {
            const value = product[fieldName];
            if (fieldName === 'description') {
                return value !== null && typeof value === 'string' && value.trim().length > 0;
            }
            if (fieldName === 'brand' || fieldName === 'category' || fieldName === 'subcategoryProduct' || fieldName === 'unit') {
                if (value === null) {
                    return false;
                }
                return typeof value === 'object' && value !== null && 'id' in value && value.id !== null && value.id !== 0;
            }
            return true;
        }

        static validateStock(stock: number): boolean{
            if (stock <= 0) {
                return false;
            }
            return true;
        }

        static validateSalePrice(price: number): boolean{
            if (price <= 0) {
                return false;
            }
            return true;
        }
}
