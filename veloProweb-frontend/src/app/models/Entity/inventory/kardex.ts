import { MovementsType } from "../../enum/movements-type";

export interface Kardex {
    date: string,
    product: string,
    stock: number,
    price: number,
    movementsType: MovementsType,
    quantity: number,
    user: string,
    comment: string,
}
