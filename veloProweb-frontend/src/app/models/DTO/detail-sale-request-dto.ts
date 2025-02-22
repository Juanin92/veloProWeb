export interface DetailSaleRequestDTO {
    descriptionProduct: string;
    quantity: number;
    price: number;
    tax: number
    customer: string;
    notification: string;
    ticketStatus: boolean;
}
