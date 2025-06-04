import { MovementsType } from "../../enum/movements-type";
import { Product } from "../product/product";
import { User } from "../user";


export interface Kardex {
    id: number;
    date: Date;
    quantity: number;
    stock: number;
    comment: string;
    price: number;
    movementsType: MovementsType;
    product: Product;
    user: User;
}
