import { Product } from "../product/product";
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
