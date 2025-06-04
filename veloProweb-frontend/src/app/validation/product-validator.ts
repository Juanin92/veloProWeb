import { Brand } from "../models/entity/product/brand";
import { Category } from "../models/entity/product/category";
import { Product } from "../models/entity/product/product";
import { Subcategory } from "../models/entity/product/subcategory";
import { UnitProduct } from "../models/entity/product/unit-product";


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
            return value !== null && typeof value === 'string' && value.trim().length > 0;
        }
        return true;
    }

    static validateStock(stock: number): boolean {
        if (stock <= 0) {
            return false;
        }
        return true;
    }

    static validateSalePrice(price: number): boolean {
        if (price <= 0) {
            return false;
        }
        return true;
    }

    static validateBrand(newBrand: Brand): boolean {
        return !!newBrand && typeof newBrand.name === 'string' && newBrand.name.trim().length >= 2;
    }

    static validateCategory(newCategory: Category): boolean {
        return newCategory.name !== null && newCategory.name.trim().length >= 3 && !/\d/.test(newCategory.name);
    }

    static validateSubcategory(newSubcategory: Subcategory): boolean {
        return newSubcategory.name !== null && newSubcategory.name.trim().length >= 3 && !/\d/.test(newSubcategory.name);
    }

    static validateUnit(newUnit: UnitProduct): boolean {
        return newUnit.nameUnit !== null && newUnit.nameUnit.trim().length >= 3 && /^[0-9]+ [a-zA-Z]+$/.test(newUnit.nameUnit);
    }
}
