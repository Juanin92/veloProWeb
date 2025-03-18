import { Component, OnInit } from '@angular/core';
import { CashRegister } from '../../../models/Entity/cash-register';
import { CommonModule } from '@angular/common';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { FormsModule } from '@angular/forms';
import { SettingPermissionsService } from '../../../services/Permissions/setting-permissions.service';

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
  sortDateOpening: boolean = true;
  sortCloseOpening: boolean = true;
  sortAmountOpening: boolean = true;
  sortAmountCloseCash: boolean = true;
  sortAmountClosePos: boolean = true;

  constructor(private cashRegisterService: CashRegisterService, 
    protected permission: SettingPermissionsService){}

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

  toggleSortOpenDate(): void{
    this.sortDateOpening = !this.sortDateOpening;
    this.filteredCashRegistersList.sort((a, b) => {
      const dateA = new Date(a.dateOpening).getTime();
      const dateB = new Date(b.dateOpening).getTime();
      return this.sortDateOpening ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortCloseDate(): void{
    this.sortCloseOpening = !this.sortCloseOpening;
    this.filteredCashRegistersList.sort((a, b) => {
      const dateA = new Date(a.dateClosing).getTime();
      const dateB = new Date(b.dateClosing).getTime();
      return this.sortCloseOpening ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortAmountOpening(): void{
    this.sortAmountOpening = !this.sortAmountOpening;
    this.filteredCashRegistersList.sort((a, b) => {
      const dateA = a.amountOpening;
      const dateB = b.amountOpening;
      return this.sortAmountOpening ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortCloseCash(): void{
    this.sortAmountCloseCash = !this.sortAmountCloseCash;
    this.filteredCashRegistersList.sort((a, b) => {
      const dateA = a.amountClosingCash;
      const dateB = b.amountClosingCash;
      return this.sortAmountCloseCash ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortClosePos(): void{
    this.sortAmountClosePos = !this.sortAmountClosePos;
    this.filteredCashRegistersList.sort((a, b) => {
      const dateA = a.amountClosingPos;
      const dateB = b.amountClosingPos;
      return this.sortAmountClosePos ? dateA - dateB : dateB - dateA;
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
