  import { Component, OnInit } from '@angular/core';
  import { Customer } from '../../models/Customer/customer.model';
  import { CustomerService } from '../../services/customer.service';
  import { CommonModule, NgStyle } from '@angular/common';
  import { FormsModule} from '@angular/forms';
  import Swal from 'sweetalert2';
import { CustomerValidator } from '../../validation/customer-validator';
import { PaymentStatus } from '../../models/enum/payment-status.enum';


  @Component({
    selector: 'app-customer',
    standalone: true,
    imports: [CommonModule, NgStyle, FormsModule],
    templateUrl: './customer.component.html',
    styleUrl: './customer.component.css'
  })
  export class CustomerComponent implements OnInit{
    customers: Customer[] = [];
    filteredCustomers: Customer[] = [];
    selectedCustomer: Customer | null = null;
    textFilter: string = '';
    totalDebts: number = 0;
    customerValidator = CustomerValidator;
    newCustomer: Customer = this.createEmptyCustomer();

    constructor(
      private customerService: CustomerService
    ){}

    ngOnInit(): void {
      this.getAllCustomer();
    }

    getAllCustomer(): void{
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

    addCustomer(): void {
      if (this.validateForm(this.newCustomer)) {
        this.customerService.addCustomer(this.newCustomer).subscribe(
          (response) => {
            console.log('Cliente agregado exitosamente:', response);
            Swal.fire({
              icon: 'success',
              title: 'Cliente agregado',
              text: `¡El cliente ${this.newCustomer.name} ${this.newCustomer.surname} fue agregado exitosamente!`,
              confirmButtonText: 'Aceptar',
            }).then(() => {
              this.createEmptyCustomer();
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

    updateCustomer(): void{
      if (this.selectedCustomer && this.validateForm(this.selectedCustomer)) {
        const updateCustomer = {...this.selectedCustomer};
        this.customerService.updateCustomer(this.selectedCustomer).subscribe(response => {
          const id = this.customers.findIndex(customer => customer.id === updateCustomer.id);
          if (id !== -1) {
            this.customers[id] = updateCustomer;
          }
          this.selectedCustomer = null;
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

    createEmptyCustomer(): Customer{
      return {
        id: 0, 
        name: '',
        surname: '',
        phone: '+569 ',
        email: '',
        debt: 0,
        totalDebt: 0, 
        status: PaymentStatus.NULO, 
        account: true, 
        paymentCustomerList: [], 
        ticketHistoryList: [] 
      };
    }

    validateForm(customer: Customer): boolean {
      return this.customerValidator.validateForm(customer);
    }

    updateTotalDebtLabel(): void{
      this.totalDebts = this.customers.reduce((sum, customer) => sum + customer.debt, 0);
    }

    statusColor(status: string): string{
      switch(status){
        case 'PAGADA': return 'rgb(40, 238, 40)';
        case 'PENDIENTE': return 'red';
        case 'PARCIAL': return 'rgb(9, 180, 237)';
        case 'VENCIDA': return 'blue';
        default: return 'transparent';
      }
    }

    getStatusAccount(account: boolean): string{
      return account ? 'Activo':'Inactivo';
    }

    getEmailEmpty(email: string): string{
      return email.includes('x@x.xxx') ? 'Sin Registro': email;
    }

    editModalCustomer(customer: Customer): void{
      if (customer) {
        this.selectedCustomer = {...customer};
      } else {
          console.error('No se pudo editar, el cliente es undefined');
      }
    }

    searchFilterCustomer(): void{
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
