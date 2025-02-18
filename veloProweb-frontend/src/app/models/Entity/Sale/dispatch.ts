import { SaleDetailDTO } from "../../DTO/sale-detail-dto";

export interface Dispatch {
    id: number,
    trackingNumber: string,
    status: string,
    address: string,
    comment: string,
    created: string,
    deliveryDate: string,
    saleDetail: SaleDetailDTO | null
}
