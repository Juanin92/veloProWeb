import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CustomerResponse } from '../../../models/Entity/Customer/customer-response';
import { CustomerService } from '../../../services/customer/customer.service';
import { CustomerValidator } from '../../../validation/customer-validator';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { CustomerPermissionsService } from '../../../services/Permissions/customer-permissions.service';
import { CustomerForm } from '../../../models/Entity/Customer/customer-form';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-update-customer',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-customer.component.html',
  styleUrl: './update-customer.component.css'
})
export class UpdateCustomerComponent {

  @Input() selectedCustomer: CustomerResponse; //Cliente seleccionado desde un componente padre
  @Output() customerUpdated = new EventEmitter<void>();
  customerValidator = CustomerValidator; //Validador de los datos del cliente
  customerDTO: CustomerForm;

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService,
    protected permission: CustomerPermissionsService,
    public modalService: ModalService,
    private errorMessage: ErrorMessageService) {
      this.selectedCustomer = customerHelper.createEmptyCustomer();
      this.customerDTO = customerHelper.createEmptyCustomerDTO();
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
      this.customerDTO = this.customerHelper.mapCustomerToDto(updateCustomer);
      this.customerService.updateCustomer(this.customerDTO).subscribe({
        next:(response) => {
          console.log('Se actualizo el cliente: ', this.customerDTO);
          this.notification.showSuccessToast(`Se actualizo el cliente ${this.customerDTO.name} ${this.customerDTO.surname} correctamente`, 'top', 3000);
          this.customerUpdated.emit();
          this.modalService.closeModal();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`Error al actualizar cliente: ${message}`, 'top', 5000);
          console.log('Errores al actualizar cliente: ', message);
        }
      });
    }
  }

  /**
   * Activa la cuenta de un cliente seleccionado
   * Valida si cliente no es nulo y si es correcto, llama al servicio para activar al cliente
   * @param customer cliente seleccionado para activar
   */
  activeCustomer(customer: CustomerResponse): void {
    if (customer) {
      this.customerDTO = this.customerHelper.mapCustomerToDto(customer);
      this.customerService.activeCustomer(this.customerDTO).subscribe({
        next:(response) => {
          console.log("Cliente Activado: ", response.message);
          this.notification.showSuccessToast(`Se activo nuevamente a ${this.customerDTO!.name} ${this.customerDTO!.surname}.`, 'top', 3000);
          this.customerUpdated.emit();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          console.log('Error al activar cliente: ', message);
          this.notification.showErrorToast(`Error al activar al cliente \n${message}`, 'top', 5000);
        }
      });
    }
  }
}
