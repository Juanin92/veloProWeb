import { SaleDetailDTO } from "../../DTO/sale-detail-dto";
import { SaleDetailRequest } from "./sale-detail-request";

export interface DispatchRequest {
    address: string,
    customer: string,
    comment: string,
    saleDetails: SaleDetailRequest[] | null
}
