import { PaymentRequestDTO } from "../models/DTO/payment-request-dto";

export class PaymentValidator {
    static validateFormPayment(payment: PaymentRequestDTO): boolean{
        return payment.customerID !== null &&
        payment.ticketIDs !== null && payment.ticketIDs.length > 0 &&
        payment.amount !== null && payment.amount > 0 &&
        payment.comment !== null && payment.comment.trim() !== '';
    }
}
