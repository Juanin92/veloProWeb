import { Component, OnInit } from '@angular/core';
import { CashRegister } from '../../../models/Entity/cash-register';
import { CommonModule } from '@angular/common';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cashier-movements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cashier-movements.component.html',
  styleUrl: './cashier-movements.component.css'
})
export class CashierMovementsComponent implements OnInit{

  cashRegistersList: CashRegister[] = [];
  filteredCashRegistersList: CashRegister[] = [];
  textFilter: string = '';

  constructor(private cashRegisterService: CashRegisterService){}

  ngOnInit(): void {
    this.getCashRegisters();
  }

  getCashRegisters(): void{
    this.cashRegisterService.getCashRegisters().subscribe({
      next:(list)=>{
        this.cashRegistersList = list;
        this.filteredCashRegistersList = list;
      },error: (error)=>{
        console.log('No se encontró información sobre los registros de caja')
      }
    });
  }

  searchFilterCashierMovements(): void {
    if (this.textFilter.trim() === '') {
      this.filteredCashRegistersList = this.cashRegistersList;
    } else {
      this.filteredCashRegistersList = this.cashRegistersList.filter(cashier =>
        cashier.user.name.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        cashier.user.surname.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        (this.textFilter.toLowerCase() === 'abierto' && cashier.status) || 
        (this.textFilter.toLowerCase() === 'cerrado' && cashier.status)
      );
    }
  }
}
