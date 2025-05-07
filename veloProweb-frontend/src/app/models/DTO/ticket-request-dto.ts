import { CustomerResponse } from "../Entity/Customer/customer-response";

export interface TicketRequestDTO {
    customer: CustomerResponse;
    number: number;
    total: number;
    date: Date;
}
