import { Product } from "../product/product";
import { Purchase } from "./purchase";

export interface PurchaseDetails {
    quantity: number;
    price: number;
    tax: number;
    total: number;
    product: Product;
}
