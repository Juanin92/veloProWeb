import { Component, OnInit } from '@angular/core';
import { Customer } from '../../models/Customer/customer.model';
import { CustomerService } from '../../services/customer.service';
import { CommonModule, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CustomerValidator } from '../../validation/customer-validator';
import { PaymentStatus } from '../../models/enum/payment-status.enum';
import { PaymentCustomerComponent } from "./payment-customer/payment-customer.component";
import { AddCustomerComponent } from "./add-customer/add-customer.component";
import { UpdateCustomerComponent } from "./update-customer/update-customer.component";
import { CustomerHelperServiceService } from '../../services/customer-helper-service.service';


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
    private customerHelper: CustomerHelperServiceService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  ngOnInit(): void {
    this.getAllCustomer();
  }

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

  deleteCustomer(customer: Customer): void {
    this.selectedCustomer = customer;
    if (this.selectedCustomer) {
      Swal.fire({
        title: "¿Estas seguro?",
        text: "No podrás revertir la acción!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Si eliminar!",
        cancelButtonText: "Cancelar",
      }).then((result) => {
        if (result.isConfirmed) {
          this.customerService.deleteCustomer(this.selectedCustomer!).subscribe((response) => {
            console.log('Cliente eliminado exitosamente:', response);
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
              title: `Se Elimino el cliente ${this.selectedCustomer!.name} ${this.selectedCustomer!.surname} correctamente`
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
              title: `Error al eliminar cliente \n${message}`
            });
          });
        }
      });
    }
  }

  editModalCustomer(customer: Customer): void {
    if (customer) {
      this.selectedCustomer = { ...customer };
    } else {
      console.error('No se pudo editar, el cliente es undefined');
    }
  }

  updateTotalDebtLabel(): void {
    this.totalDebts = this.customers.reduce((sum, customer) => sum + customer.debt, 0);
  }

  statusColor(status: string): string {
    switch (status) {
      case 'PAGADA': return 'rgb(40, 238, 40)';
      case 'PENDIENTE': return 'red';
      case 'PARCIAL': return 'rgb(9, 180, 237)';
      case 'VENCIDA': return 'blue';
      default: return 'transparent';
    }
  }

  getStatusAccount(account: boolean): string {
    return account ? 'Activo' : 'Inactivo';
  }

  getEmailEmpty(email: string): string {
    return email.includes('x@x.xxx') ? 'Sin Registro' : email;
  }

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
