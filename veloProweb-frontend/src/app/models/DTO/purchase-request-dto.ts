import { Supplier } from "../Entity/Purchase/supplier";

export interface PurchaseRequestDTO {
    id: number;
    date: string;
    supplier: Supplier | null;
    documentType: string;
    document: string;
    tax: number;
    total: number;
    detailList: PurchaseRequestDTO[];
}
