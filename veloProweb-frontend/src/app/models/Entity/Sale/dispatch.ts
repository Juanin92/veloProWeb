import { Sale } from "./sale";

export interface Dispatch {
    id: string,
    trackingNumber: string,
    status: string,
    created: string,
    deliveryDate: string,
    sale: Sale,
}
