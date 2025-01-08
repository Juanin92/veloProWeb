import { Supplier } from "./supplier";

export interface Purchase {
    id: number;
    document: string;
    documentType: string;
    tax: number;
    purchaseTotal: number;
    date: string;
    supplier: Supplier;
}
