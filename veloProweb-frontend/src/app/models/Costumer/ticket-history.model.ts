import { Costumer } from "./costumer.model";

export interface TicketHistory {
    id: number;
    amount: number;
    document: string;
    total: number;
    status: boolean;
    date: string;
    notificationsDate: string;
    costumer: Costumer;
}
