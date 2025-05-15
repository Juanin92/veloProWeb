import { Product } from "../Product/product";
import { Purchase } from "./purchase";

export interface PurchaseDetails {
    id: number;
    quantity: number;
    price: number;
    tax: number;
    total: number;
    purchase: Purchase;
    product: Product;
}
