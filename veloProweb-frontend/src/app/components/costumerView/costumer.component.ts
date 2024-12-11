  import { Component, OnInit } from '@angular/core';
  import { Costumer } from '../../models/Costumer/costumer.model';
  import { CostumerService } from '../../services/costumer.service';
  import { CommonModule, NgStyle } from '@angular/common';
  import { FormsModule} from '@angular/forms';
  import Swal from 'sweetalert2';
import { CostumerValidator } from '../../validation/costumer-validator';
import { PaymentStatus } from '../../models/enum/payment-status.enum';


  @Component({
    selector: 'app-costumer',
    standalone: true,
    imports: [CommonModule, NgStyle, FormsModule],
    templateUrl: './costumer.component.html',
    styleUrl: './costumer.component.css'
  })
  export class CostumerComponent implements OnInit{
    costumers: Costumer[] = [];
    filteredCostumers: Costumer[] = [];
    selectedCostumer: Costumer | null = null;
    textFilter: string = '';
    totalDebts: number = 0;
    costumerValidator = CostumerValidator;
    newCostumer: Costumer = this.createEmptyCostumer();

    constructor(
      private costumerService: CostumerService
    ){}

    ngOnInit(): void {
      this.getAllCostumer();
    }

    getAllCostumer(): void{
      this.costumerService.getCostumer().subscribe(
        (data) => {
          this.costumers = data;
          this.filteredCostumers = data;
          this.updateTotalDebtLabel();
        }, (error) => {
          console.log('Error no se encontró ningún cliente', error);
        }
      );
    }

    addCostumer(): void {
      if (this.validateForm(this.newCostumer)) {
        this.costumerService.addCostumer(this.newCostumer).subscribe(
          (response) => {
            console.log('Cliente agregado exitosamente:', response);
            Swal.fire({
              icon: 'success',
              title: 'Cliente agregado',
              text: `¡El cliente ${this.newCostumer.name} ${this.newCostumer.surname} fue agregado exitosamente!`,
              confirmButtonText: 'Aceptar',
            }).then(() => {
              this.createEmptyCostumer();
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

    updateCostumer(): void{
      if (this.selectedCostumer && this.validateForm(this.selectedCostumer)) {
        const updateCostumer = {...this.selectedCostumer};
        this.costumerService.updateCostumer(this.selectedCostumer).subscribe((data) => {
          const id = this.costumers.findIndex(costumer => costumer.id === data.id);
          if (id !== -1) {
            this.costumers[id] = data;
          }
          this.selectedCostumer = null;
          console.log('Se actualizo el cliente: ', data);
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
            title: `Se actualizo el cliente ${updateCostumer.name} ${updateCostumer.surname} correctamente`
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

    createEmptyCostumer(): Costumer{
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
        paymentCostumerList: [], 
        ticketHistoryList: [] 
      };
    }

    validateForm(costumer: Costumer): boolean {
      return this.costumerValidator.validateForm(costumer);
    }

    updateTotalDebtLabel(): void{
      this.totalDebts = this.costumers.reduce((sum, costumer) => sum + costumer.debt, 0);
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

    editModalCostumer(costumer: Costumer): void{
      if (costumer) {
        this.selectedCostumer = {...costumer};
      } else {
          console.error('No se pudo editar, el cliente es undefined');
      }
    }

    searchFilterCostumer(): void{
      if (this.textFilter.trim() === '') {
        this.filteredCostumers = this.costumers;
      } else {
        this.filteredCostumers = this.costumers.filter(costumer => 
          costumer.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
          costumer.surname.toLowerCase().includes(this.textFilter.toLowerCase()) ||
          costumer.status.toLowerCase().includes(this.textFilter.toLowerCase()) ||
          (this.textFilter.toLowerCase() === 'activo' && costumer.account) || 
          (this.textFilter.toLowerCase() === 'inactivo' && !costumer.account)
        );
      }
    }
  }
