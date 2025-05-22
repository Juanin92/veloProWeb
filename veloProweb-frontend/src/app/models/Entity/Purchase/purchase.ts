import { Supplier } from "./supplier";

export interface Purchase {
    document: string;
    documentType: string;
    tax: number;
    total: number;
    date: string;
    supplier: Supplier | null;
}
