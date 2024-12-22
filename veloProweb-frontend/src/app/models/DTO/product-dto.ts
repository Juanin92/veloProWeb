import { StatusProduct } from "../enum/status-product";

export interface ProductDTO {
    id: number;
    description: string;
    salePrice: number;
    buyPrice: number;
    stock: number;
    status: boolean;
    statusProduct: StatusProduct;
    brand: string;
    unit: string;
    subcategoryProduct: string;
    category: string;
}
