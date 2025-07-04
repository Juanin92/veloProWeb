import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CommonModule } from '@angular/common';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { CustomerPermissionsService } from '../../../services/permissions/customer-permissions.service';
import { CustomerForm } from '../../../models/entity/customer/customer-form';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.css'
})
export class AddCustomerComponent {

  newCustomer: CustomerForm;
  customerValidator = CustomerValidator; // Validador de para los datos del cliente.
  @Output() customerAdded = new EventEmitter<void>();
  touchedFields: Record<string, boolean> = {}; //Campo tocado en el DOM 

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService,
    protected permission: CustomerPermissionsService,
    private errorMessage: ErrorMessageService,
    public modalService: ModalService) {
    this.newCustomer = customerHelper.createEmptyCustomerDTO();
    this.touchedFields = {};
  }

  /**
   * Agregar un nuevo cliente.
   * Valida el formulario y si es correcto, llama al servicio para agregar cliente.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la lista de clientes.
   */
  addCustomer(): void {
    if (this.customerValidator.validateForm(this.newCustomer)) {
      this.customerService.addCustomer(this.newCustomer).subscribe({
        next: (response) =>{
          console.log('Cliente agregado exitosamente:', response);
          this.notification.showSuccessToast(`¡El cliente ${this.newCustomer.name} ${this.newCustomer.surname} fue agregado exitosamente!`, 'top', 3000);
          this.customerHelper.createEmptyCustomerDTO(); 
          this.customerAdded.emit();
          this.modalService.closeModal();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          console.error('Error al agregar el cliente:', message);
          this.notification.showErrorToast(`Error al agregar cliente: ${message}`, 'top', 5000);
        }
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Reset el valor de todos los atributo del cliente 
   */
  resetCustomerForm(): void{
    this.newCustomer = this.customerHelper.createEmptyCustomerDTO();
    this.touchedFields = {};
  }
}
