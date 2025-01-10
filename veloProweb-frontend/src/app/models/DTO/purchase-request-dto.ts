import { Supplier } from "../Entity/Purchase/supplier";
import { PurchaseDetailDTO } from "./purchase-detail-dto";

export interface PurchaseRequestDTO {
    id: number;
    date: string;
    supplier: Supplier | null;
    documentType: string;
    document: string;
    tax: number;
    total: number;
    detailList: PurchaseDetailDTO[];
}
