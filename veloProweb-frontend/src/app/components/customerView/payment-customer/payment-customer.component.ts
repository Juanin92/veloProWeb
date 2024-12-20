import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { PaymentCustomerService } from '../../../services/payment-customer.service';
import { Customer } from '../../../models/Customer/customer.model';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';
import { PaymentCustomer } from '../../../models/Customer/payment-customer.model';
import { CommonModule } from '@angular/common';
import { TicketHistoryService } from '../../../services/ticket-history.service';
import { TicketHistory } from '../../../models/Customer/ticket-history.model';
import { tick } from '@angular/core/testing';

@Component({
  selector: 'app-payment-customer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-customer.component.html',
  styleUrl: './payment-customer.component.css'
})
export class PaymentCustomerComponent implements OnChanges {

  @Input() selectedCustomer: Customer;
  payments: PaymentCustomer[] = [];
  tickets: TicketHistory[] = [];
  totalDebt: number = 0;
  debtValue: number = 0;
  paymentValue: number = 0;
  selectedTickets: number[] = [];
  

  constructor(
    private paymentService: PaymentCustomerService,
    private customerHelper: CustomerHelperServiceService,
    private ticketService: TicketHistoryService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedCustomer'] && changes['selectedCustomer'].currentValue) { 
      this.debtValue = 0;
      this.selectedTickets = [];
      this.getPayments(changes['selectedCustomer'].currentValue); 
    }
  }

  getPayments(customer: Customer): void {
    this.paymentService.getCustomerSelectedPayment(customer.id).subscribe((paymentList) => {
      this.payments = paymentList;
      this.updatePaymentValueLabel();
      this.updateTotalDebtLabel();
      this.getListTicketByCustomer(customer.id);
    }, (error) => {
      console.log('Error no se encontró información de pagos ', error);
    });
  }

  getListTicketByCustomer(id: number): void{
    this.ticketService.getListTicketByCustomer(this.selectedCustomer.id).subscribe((ticketList) => {
      this.tickets = ticketList;
    }, (error) => {
      console.log('Error no se encontró información de los tickets ', error);
    });
  } 

  updateDebtValueLabel(ticket: TicketHistory, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    const payment = this.payments.find(payment => payment.document.id === ticket.id);
    const ticketAmount =  payment ? ticket.total - payment.amount : ticket.total;
    if (checkbox.checked) {
      this.selectedTickets.push(ticketAmount);
    }else{
      const index = this.selectedTickets.indexOf(ticketAmount);
      if (index > -1) {
          this.selectedTickets.splice(index, 1);
      }
    }
    this.debtValue = Array.from(this.selectedTickets).reduce((collector, current) => collector + current, 0);
  }

  updatePaymentValueLabel(): void {
    this.paymentValue = this.payments.reduce((sum, payment) => sum + payment.amount, 0);
  }

  updateTotalDebtLabel(): void {
    this.totalDebt = this.selectedCustomer.debt;
  }
}
