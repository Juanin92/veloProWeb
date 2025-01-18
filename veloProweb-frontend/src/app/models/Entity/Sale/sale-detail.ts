import { Product } from "../Product/product.model";
import { Sale } from "./sale";

export interface SaleDetail {
    id: number;
    quantity: number;
    price: number;
    tax: number;
    total: number;
    sale: Sale;
    product: Product;
}
