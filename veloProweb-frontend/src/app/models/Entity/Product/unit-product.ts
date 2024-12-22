import { Product } from "./product.model";

export interface UnitProductModel {
    id: number;
    name: string;
    products: Product[];
}
