import { Component, OnInit } from '@angular/core';
import { Costumer } from '../../models/Costumer/costumer.model';
import { CostumerService } from '../../services/costumer.service';
import { CommonModule, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';


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

  constructor(
    private costumerService: CostumerService, 
    private router: Router,
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

  updateCostumer(): void{
    if (this.selectedCostumer) {
      this.costumerService.updateCostumer(this.selectedCostumer).subscribe((data) => {
        const id = this.costumers.findIndex(costumer => costumer.id === data.id);
        if (id !== -1) {
          this.costumers[id] = data;
        }
        this.selectedCostumer = null;
        console.log('Se actualizo el cliente: ');
        Swal.fire({
          position: "top",
          icon: "success",
          title: 'Se actualizo el cliente',
          showConfirmButton: false,
          timer: 1500
        }).then(() => {
          this.router.navigate(['/clientes']).then(() => {
            window.location.reload();
          });
        });
      },
      (error) => {
        console.log('Error al actualizar cliente: ', error);
        Swal.fire({
          position: "top",
          icon: "error",
          title: 'Error al actualizar cliente: ' + error,
          showConfirmButton: false,
          timer: 1500
        });
      }
    );
    }
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
