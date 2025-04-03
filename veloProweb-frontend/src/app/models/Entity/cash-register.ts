export interface CashRegister {
    dateOpening: string;
    dateClosing: string;
    amountOpening: number;
    amountClosingCash: number;
    amountClosingPos: number;
    status: string;
    comment: string;
    alert: boolean;
    user: string;
}
