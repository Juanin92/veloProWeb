import { User } from "./user";

export interface CashRegister {
    id: number;
    dateOpening: string;
    dateClosing: string;
    amountOpening: number;
    amountClosingCash: number;
    amountClosingPos: number;
    status: boolean;
    comment: string;
    user: User;
}
