import { Component } from '@angular/core';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { CashRegister } from '../../../models/Entity/cash-register';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cashier',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cashier.component.html',
  styleUrl: './cashier.component.css'
})
export class CashierComponent {

  cashier: CashRegister = this.initializeCashier();
  canSale: boolean = false;
  isOpening: boolean = true;
  isClosing: boolean = false;

  constructor(
    private cashierService: CashRegisterService){}

  saveOpeningRegister(): void{
    this.isOpening = false;
    this.isClosing = true;
  }

  saveClosingRegister(): void{}

  initializeCashier(): CashRegister{
    return {
      dateOpening: '',
      dateClosing: '',
      amountOpening: 0,
      amountClosingCash: 0,
      amountClosingPos: 0,
      status: false,
      comment: '',
      user: ''
    }
  }
}
