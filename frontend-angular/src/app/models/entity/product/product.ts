import { StatusProduct } from "../../enum/status-product";

export interface Product {
    id: number;
    description: string;
    salePrice: number;
    buyPrice: number;
    stock: number;
    reserve: number;
    statusProduct: StatusProduct;
    brand: string;
    unit: string;
    subcategoryProduct: string;
    category: string;
}
