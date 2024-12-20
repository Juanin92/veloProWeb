import { Component, OnInit } from '@angular/core';
import { Customer } from '../../models/Entity/Customer/customer.model';
import { CustomerService } from '../../services/customer/customer.service';
import { CommonModule, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentCustomerComponent } from "./payment-customer/payment-customer.component";
import { AddCustomerComponent } from "./add-customer/add-customer.component";
import { UpdateCustomerComponent } from "./update-customer/update-customer.component";
import { CustomerHelperServiceService } from '../../services/customer/customer-helper-service.service';
import { NotificationService } from '../../utils/notification-service.service';


@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, NgStyle, FormsModule, PaymentCustomerComponent, AddCustomerComponent, UpdateCustomerComponent],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css'
})
export class CustomerComponent implements OnInit {
  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  selectedCustomer: Customer;

  textFilter: string = '';
  totalDebts: number = 0;

  constructor(
    private customerService: CustomerService,
    private customerHelper: CustomerHelperServiceService,
    private notification: NotificationService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  ngOnInit(): void {
    this.getAllCustomer();
  }

  /**
   * Obtienes todos los clientes desde el servicio
   * Asigna una lista de cliente a la lista de cliente filtrada y normal
   * llama al método para calcular el total de las deudas
   */
  getAllCustomer(): void {
    this.customerService.getCustomer().subscribe(
      (data) => {
        this.customers = data;
        this.filteredCustomers = data;
        this.updateTotalDebtLabel();
      }, (error) => {
        console.log('Error no se encontró ningún cliente', error);
      }
    );
  }

  /**
   * Elimina (Desactivar) la cuenta de un cliente seleccionado 
   * Valida si el cliente no es valor nulo, si es correcto
   * lanza una confirmación antes de eliminar al cliente
   * @param customer - cliente seleccionado
   */
  deleteCustomer(customer: Customer): void {
    this.selectedCustomer = customer;
    if (this.selectedCustomer) {
      this.notification.showConfirmation(
        "¿Estas seguro?",
        "No podrás revertir la acción!",
        "Si eliminar!",
        "Cancelar"
      ).then((result) => {
        if (result.isConfirmed) {
          this.customerService.deleteCustomer(this.selectedCustomer!).subscribe((response) => {
            console.log('Cliente eliminado exitosamente:', response);
            this.notification.showSuccessToast(`Se Elimino el cliente ${this.selectedCustomer!.name} ${this.selectedCustomer!.surname} correctamente`, 'top', 3000);
            setTimeout(() => {
              window.location.reload();
            }, 3000);
          }, (error) => {
            const message = error.error.error;
            console.log('Error al eliminar cliente: ', message);
            this.notification.showErrorToast(`Error al eliminar cliente \n${message}`, 'top', 5000);
          });
        }
      });
    }
  }

  /**
   * Abrir modal con una copia de un cliente seleccionado
   * @param customer - cliente seleccionado
   */
  openModalCustomer(customer: Customer): void {
    if (customer) {
      this.selectedCustomer = { ...customer };
    } else {
      console.error('No se pudo abrir modal, el cliente es undefined');
    }
  }

  /**
   * Actualiza la suma de las deudas de los clientes
   */
  updateTotalDebtLabel(): void {
    this.totalDebts = this.customers.reduce((sum, customer) => sum + customer.debt, 0);
  }

  /**
   * Asigna un color dependiendo el status de cada cliente 
   * @param status - status del cliente dependiendo de la deuda
   * @returns - Retorna el color asociado al status
   */
  statusColor(status: string): string {
    switch (status) {
      case 'PAGADA': return 'rgb(40, 238, 40)';
      case 'PENDIENTE': return 'red';
      case 'PARCIAL': return 'rgb(9, 180, 237)';
      case 'VENCIDA': return 'blue';
      default: return 'transparent';
    }
  }

  /**
   * Reasigna el valor en una cadena según el estado de la cuenta
   * @param account - valor de la cuenta del cliente
   * @returns - Devuelve un valor 'activo' si es true o si es false sera 'Inactivo'
   */
  getStatusAccount(account: boolean): string {
    return account ? 'Activo' : 'Inactivo';
  }

  /**
   * Verifica si email esta vacío para dar un valor más representativo
   * @param email - email del cliente
   * @returns - Devuelve 'Sin Registro' si esta vacío si no el valor original del email
   */
  getEmailEmpty(email: string): string {
    return email.includes('x@x.xxx') ? 'Sin Registro' : email;
  }

  /**
   * Filtrar lista de cliente según el criterio de búsqueda
   * Se filtrara por nombre, apellido, estado del cliente donde textFilter
   * contendrá el valor a filtrar
   */
  searchFilterCustomer(): void {
    if (this.textFilter.trim() === '') {
      this.filteredCustomers = this.customers;
    } else {
      this.filteredCustomers = this.customers.filter(customer =>
        customer.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        customer.surname.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        customer.status.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        (this.textFilter.toLowerCase() === 'activo' && customer.account) ||
        (this.textFilter.toLowerCase() === 'inactivo' && !customer.account)
      );
    }
  }
}
