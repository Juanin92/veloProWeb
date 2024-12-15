import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Customer } from '../../../models/Customer/customer.model';
import { PaymentStatus } from '../../../models/enum/payment-status.enum';
import { CustomerService } from '../../../services/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';

@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.css'
})
export class AddCustomerComponent {

  newCustomer: Customer;
  customerValidator = CustomerValidator;

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService) {
    this.newCustomer = customerHelper.createEmptyCustomer();
  }

  addCustomer(): void {
    if (this.customerValidator.validateForm(this.newCustomer)) {
      this.customerService.addCustomer(this.newCustomer).subscribe(
        (response) => {
          console.log('Cliente agregado exitosamente:', response);
          Swal.fire({
            icon: 'success',
            title: 'Cliente agregado',
            text: `¡El cliente ${this.newCustomer.name} ${this.newCustomer.surname} fue agregado exitosamente!`,
            confirmButtonText: 'Aceptar',
          }).then(() => {
            this.customerHelper.createEmptyCustomer();
            window.location.reload();
          });
        },
        (error) => {
          console.error('Error al agregar el cliente:', error);
          Swal.fire({
            icon: 'error',
            title: 'Error al agregar cliente',
            text: error.error.message || 'Ocurrió un error inesperado.',
            confirmButtonText: 'Aceptar',
          });
        }
      );
    } else {
      Swal.fire({
        icon: 'warning',
        title: 'Formulario incompleto',
        text: 'Por favor, complete correctamente todos los campos obligatorios.',
        confirmButtonText: 'Aceptar',
      });
    }
  }
}
