import { Category } from "./category";
import { Product } from "./product.model";

export interface Subcategory {
    id: number;
    name: string;
    products: Product[];
    category: Category;
}
