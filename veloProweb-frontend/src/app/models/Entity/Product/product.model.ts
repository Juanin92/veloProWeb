import { Brand } from "./brand";
import { Category } from "./category";
import { Subcategory } from "./subcategory";
import { UnitProductModel } from "./unit-product";

export interface Product {
    id: number;
    description: string;
    salePrice: number;
    buyPrice: number;
    stock: number;
    status: boolean;
    // statusProduct: StatusProduct;
    brand: Brand;
    unit: UnitProductModel;
    subcategoryProduct: Subcategory;
    category: Category;
    // kardexList: Kardex[];
}
