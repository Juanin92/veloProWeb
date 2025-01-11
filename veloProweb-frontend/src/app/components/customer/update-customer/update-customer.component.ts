import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { CustomerService } from '../../../services/customer/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-update-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-customer.component.html',
  styleUrl: './update-customer.component.css'
})
export class UpdateCustomerComponent {

  @Input() selectedCustomer: Customer; //Cliente seleccionado desde un componente padre
  @Output() customerUpdated = new EventEmitter<void>();
  customerValidator = CustomerValidator; //Validador de los datos del cliente

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService) {
    //Se inicializa la variable con valores vacíos mediante el helper
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  /**
   * Actualiza los datos de un cliente seleccionado
   * Valida que el cliente seleccionado no sea nulo y se valida el formulario.
   * Si todo es correcto, llama el servicio para actualizar al cliente
   * Muestra notificaciones dependiendo el estado de la acción y refresca la página después de 3 seg.
   */
  updateCustomer(): void {
    if (this.selectedCustomer && this.customerValidator.validateForm(this.selectedCustomer)) {
      const updateCustomer = { ...this.selectedCustomer }; // Crea una copia del cliente seleccionado para modificar
      this.customerService.updateCustomer(updateCustomer).subscribe((response) => {
        console.log('Se actualizo el cliente: ', updateCustomer);
        this.notification.showSuccessToast(`Se actualizo el cliente ${updateCustomer.name} ${updateCustomer.surname} correctamente`, 'top', 3000);
        this.customerUpdated.emit();
      },
        (error) => {
          const message = error.error.error;
          this.notification.showErrorToast(`Error al actualizar cliente \n${message}`, 'top', 5000);
          console.log('Error al actualizar cliente: ', message);
        }
      );
    }
  }

  /**
   * Activa la cuenta de un cliente seleccionado
   * Valida si cliente no es nulo y si es correcto, llama al servicio para activar al cliente
   * @param customer cliente seleccionado para activar
   */
  activeCustomer(customer: Customer): void {
    if (customer) {
      this.customerService.activeCustomer(customer).subscribe((response) => {
        console.log("Cliente Activado");
        this.notification.showSuccessToast(`Se activo nuevamente a ${customer!.name} ${customer!.surname}.`, 'top', 3000);
        this.customerUpdated.emit();
      }, (error) => {
        const message = error.error.error;
        console.log('Error al activar cliente: ', message);
        this.notification.showErrorToast(`Error al activar al cliente \n${message}`, 'top', 5000);
      })
    }
  }
}
