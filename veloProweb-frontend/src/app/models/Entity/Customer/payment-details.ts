export interface PaymentDetails {
    ticketIDs: number[];
    customerID: number;
    amount: number;
    comment: string;
    totalPaymentPaid: number;
}
