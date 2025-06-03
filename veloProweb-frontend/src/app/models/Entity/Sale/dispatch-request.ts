import { SaleDetailDTO } from "../../DTO/sale-detail-dto";

export interface DispatchRequest {
    address: string,
    customer: string,
    comment: string,
    detailSaleDTOList: SaleDetailDTO[] | null
}
