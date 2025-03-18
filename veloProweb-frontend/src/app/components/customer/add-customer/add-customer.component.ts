import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { CustomerService } from '../../../services/customer/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CommonModule } from '@angular/common';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { CustomerPermissionsService } from '../../../services/Permissions/customer-permissions.service';

@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.css'
})
export class AddCustomerComponent {

  newCustomer: Customer;
  customerValidator = CustomerValidator; // Validador de para los datos del cliente.
  @Output() customerAdded = new EventEmitter<void>();
  touchedFields: Record<string, boolean> = {}; //Campo tocado en el DOM 
  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService,
    protected permission: CustomerPermissionsService,
    public modalService: ModalService) {
    //Se inicializa la variable con valores vacíos mediante el helper
    this.newCustomer = customerHelper.createEmptyCustomer();
    this.touchedFields = {};
  }

  /**
   * Agregar un nuevo cliente.
   * Valida el formulario y si es correcto, llama al servicio para agregar cliente.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la lista de clientes.
   */
  addCustomer(): void {
    if (this.customerValidator.validateForm(this.newCustomer)) {
      this.customerService.addCustomer(this.newCustomer).subscribe(
        (response) => {
          console.log('Cliente agregado exitosamente:', response);
          this.notification.showSuccessToast(`¡El cliente ${this.newCustomer.name} ${this.newCustomer.surname} fue agregado exitosamente!`, 'top', 3000);
          this.customerHelper.createEmptyCustomer(); // Reinicia la variable del cliente vacío.
          this.customerAdded.emit();
          this.modalService.closeModal();
        },
        (error) => {
          const message = error.error?.message || error.error?.error;
          console.error('Error al agregar el cliente:', error);
          this.notification.showErrorToast(`Error al agregar cliente \n${message}`, 'top', 5000);
        }
      );
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Reset el valor de todos los atributo del cliente 
   */
  resetCustomerForm(): void{
    this.newCustomer = this.customerHelper.createEmptyCustomer();
    this.touchedFields = {};
  }
}
