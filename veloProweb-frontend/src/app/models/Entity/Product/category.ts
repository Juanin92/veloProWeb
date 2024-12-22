import { Product } from "./product.model";
import { Subcategory } from "./subcategory";

export interface Category {
    id: number;
    name: string;
    products: Product[];
    subcategory: Subcategory[];
}
