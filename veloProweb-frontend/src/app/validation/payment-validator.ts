import { PaymentDetails } from "../models/Entity/Customer/payment-details";

export class PaymentValidator {
    static validateFormPayment(payment: PaymentDetails): boolean{
        return payment.customerID !== null &&
        payment.ticketIDs !== null && payment.ticketIDs.length > 0 &&
        payment.amount !== null && payment.amount > 0 &&
        payment.comment !== null && payment.comment.trim() !== '';
    }
}
