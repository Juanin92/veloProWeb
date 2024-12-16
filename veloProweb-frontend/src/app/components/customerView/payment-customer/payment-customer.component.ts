import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { PaymentCustomerService } from '../../../services/payment-customer.service';
import { Customer } from '../../../models/Customer/customer.model';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';
import { PaymentCustomer } from '../../../models/Customer/payment-customer.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-payment-customer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-customer.component.html',
  styleUrl: './payment-customer.component.css'
})
export class PaymentCustomerComponent implements OnInit, OnChanges {

  @Input() selectedCustomer: Customer;
  payments: PaymentCustomer[] = [];
  totalDebt: number = 0;
  debtValue: number = 0;
  paymentValue: number = 0;
  

  constructor(
    private paymentService: PaymentCustomerService,
    private customerHelper: CustomerHelperServiceService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  ngOnInit(): void {
    this.updateDebtValueLabel();
    this.updatePaymentValueLabel();
    this.updateTotalDebtLabel();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedCustomer'] && changes['selectedCustomer'].currentValue) { 
      this.getPayments(changes['selectedCustomer'].currentValue); 
    }
  }

  getPayments(customer: Customer): void {
    this.paymentService.getCustomerSelectedPayment(customer.id).subscribe((paymentList) => {
      this.payments = paymentList;
      this.updateDebtValueLabel();
      this.updatePaymentValueLabel();
      this.updateTotalDebtLabel();
    }, (error) => {
      console.log('Error no se encontró información de pagos ', error);
    });
  }

  updatePaymentValueLabel(): void {
    this.paymentValue = this.payments.reduce((sum, payment) => sum + payment.amount, 0);
  }

  updateDebtValueLabel(): void {
    this.debtValue = this.selectedCustomer.debt;
  }

  updateTotalDebtLabel(): void {
    this.totalDebt = this.selectedCustomer.debt;
  }
}
