import { Sale } from "./sale";

export interface Dispatch {
    id: number,
    trackingNumber: string,
    status: string,
    created: string,
    deliveryDate: string,
    sale: Sale | null,
}
