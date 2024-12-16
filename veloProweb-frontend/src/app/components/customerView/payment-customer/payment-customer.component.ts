import { Component, Input } from '@angular/core';
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
export class PaymentCustomerComponent {

  @Input() selectedCustomer: Customer;
  payments: PaymentCustomer[] = [];

  constructor(
    private paymentService: PaymentCustomerService,
    private customerHelper: CustomerHelperServiceService){
      this.selectedCustomer = customerHelper.createEmptyCustomer();
    }

  getPayments(customer: Customer): void{
    this.paymentService.getCustomerSelectedPayment(customer.id).subscribe((list) =>{
      this.payments = list;

    }, (error) =>{
      console.log('Error no se encontró información de pagos ', error);
    });
  }
}
