import { Customer } from "../Entity/Customer/customer.model";

export interface TicketRequestDTO {
    customer: Customer;
    number: number;
    total: number;
    date: Date;
}
