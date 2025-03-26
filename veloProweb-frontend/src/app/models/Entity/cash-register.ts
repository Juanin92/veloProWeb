import { UserDTO } from "../DTO/user-dto";
import { User } from "./user";

export interface CashRegister {
    dateOpening: string;
    dateClosing: string;
    amountOpening: number;
    amountClosingCash: number;
    amountClosingPos: number;
    status: boolean;
    comment: string;
    user: string;
}
