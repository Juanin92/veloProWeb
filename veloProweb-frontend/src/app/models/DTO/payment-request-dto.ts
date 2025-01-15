export interface PaymentRequestDTO {
    ticketIDs: number[];
    customerID: number;
    amount: number;
    comment: string;
    totalPaymentPaid: number;
}
