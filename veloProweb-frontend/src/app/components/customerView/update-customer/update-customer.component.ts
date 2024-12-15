import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Customer } from '../../../models/Customer/customer.model';
import Swal from 'sweetalert2';
import { CustomerService } from '../../../services/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-update-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-customer.component.html',
  styleUrl: './update-customer.component.css'
})
export class UpdateCustomerComponent {

  @Input() selectedCustomer: Customer;
  customerValidator = CustomerValidator;

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  updateCustomer(): void {
    if (this.selectedCustomer && this.customerValidator.validateForm(this.selectedCustomer)) {
      const updateCustomer = { ...this.selectedCustomer };
      this.customerService.updateCustomer(updateCustomer).subscribe((response) => {
        console.log('Se actualizo el cliente: ', updateCustomer);
        this.notification.showSuccessToast(`Se actualizo el cliente ${updateCustomer.name} ${updateCustomer.surname} correctamente`, 'top', 3000);
        setTimeout(() => {
          window.location.reload();
        }, 3000);
      },
        (error) => {
          const message = error.error.error;
          this.notification.showErrorToast(`Error al actualizar cliente \n${message}`, 'top', 5000);
          console.log('Error al actualizar cliente: ', message);
        }
      );
    }
  }

  activeCustomer(customer: Customer): void {
    this.selectedCustomer = customer;
    if (this.selectedCustomer) {
      this.customerService.activeCustomer(this.selectedCustomer).subscribe((response) => {
        console.log("Cliente Activado");
        this.notification.showSuccessToast(`Se activo nuevamente a ${this.selectedCustomer!.name} ${this.selectedCustomer!.surname}.`,'top', 3000);
        setTimeout(() => {
          window.location.reload();
        }, 3000);
      }, (error) => {
        const message = error.error.error;
        console.log('Error al eliminar cliente: ', message);
        this.notification.showErrorToast(`Error al activar al cliente \n${message}`,'top',5000);
      })
    }
  }
}
