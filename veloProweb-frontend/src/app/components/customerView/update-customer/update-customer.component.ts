import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Customer } from '../../../models/Customer/customer.model';
import { PaymentStatus } from '../../../models/enum/payment-status.enum';
import Swal from 'sweetalert2';
import { CustomerService } from '../../../services/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';

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
    private customerHelper: CustomerHelperServiceService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  updateCustomer(): void {
    if (this.selectedCustomer && this.customerValidator.validateForm(this.selectedCustomer)) {
      const updateCustomer = { ...this.selectedCustomer };
      this.customerService.updateCustomer(updateCustomer).subscribe((response) => {
        console.log('Se actualizo el cliente: ', updateCustomer);
        const Toast = Swal.mixin({
          toast: true,
          position: "top",
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
          didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
          }
        });
        Toast.fire({
          icon: "success",
          title: `Se actualizo el cliente ${updateCustomer.name} ${updateCustomer.surname} correctamente`
        }).then(() => {
          window.location.reload();
        });
      },
        (error) => {
          const message = error.error.error;
          console.log('Error al actualizar cliente: ', message);
          const Toast = Swal.mixin({
            toast: true,
            position: "top",
            showConfirmButton: false,
            timer: 5000,
            timerProgressBar: true,
            didOpen: (toast) => {
              toast.onmouseenter = Swal.stopTimer;
              toast.onmouseleave = Swal.resumeTimer;
            }
          });
          Toast.fire({
            icon: "error",
            title: `Error al actualizar cliente \n${message}`
          });
        }
      );
    }
  }

  activeCustomer(customer: Customer): void {
    this.selectedCustomer = customer;
    if (this.selectedCustomer) {
      this.customerService.activeCustomer(this.selectedCustomer).subscribe((response) => {
        console.log("Cliente Activado");
        const Toast = Swal.mixin({
          toast: true,
          position: "top",
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
          didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
          }
        });
        Toast.fire({
          icon: "success",
          title: `Se activo nuevamente a ${this.selectedCustomer!.name} ${this.selectedCustomer!.surname}.`
        }).then(() => {
          window.location.reload();
        });
      }, (error) => {
        const message = error.error.error;
        console.log('Error al eliminar cliente: ', message);
        const Toast = Swal.mixin({
          toast: true,
          position: "top",
          showConfirmButton: false,
          timer: 5000,
          timerProgressBar: true,
          didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
          }
        });
        Toast.fire({
          icon: "error",
          title: `Error al activar al cliente \n${message}`
        });
      })
    }
  }
}
