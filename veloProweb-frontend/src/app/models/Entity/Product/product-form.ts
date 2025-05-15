import { Brand } from "./brand";
import { Category } from "./category";
import { Subcategory } from "./subcategory";
import { UnitProduct } from "./unit-product";

export interface ProductForm {
    
    description: string;
    brand: Brand;
    unit: UnitProduct;
    category: Category;
    subcategoryProduct: Subcategory;
}
