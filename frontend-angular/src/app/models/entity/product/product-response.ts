import { StatusProduct } from "../../enum/status-product";

export interface ProductResponse {
    id: number;
    description: string;
    salePrice: number;
    buyPrice: number;
    stock: number;
    reserve: number;
    threshold: number;
    statusProduct: StatusProduct;
    brand: string;
    unit: string;
    subcategoryProduct: string;
    category: string;
}
