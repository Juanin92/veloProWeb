import { Component } from '@angular/core';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { CashRegister } from '../../../models/Entity/cash-register';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-cashier',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cashier.component.html',
  styleUrl: './cashier.component.css'
})
export class CashierComponent {

  cashier: CashRegister = this.initializeCashier();

  constructor(
    private cashierService: CashRegisterService,
    private router: Router,
    private notification: NotificationService){}

  saveOpeningRegister(): void {
    if (this.cashier.amountOpening !== null) {
      this.cashierService.addRegisterOpening(this.cashier.amountOpening).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.router.navigate(['/main/ventas']);
          if(response){
            sessionStorage.setItem('isOpen', true.toString());
          }
        },
        error: (error) => {
          const message = error.error?.error || error.error?.message || error?.error;
          console.log('Error: ', message);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  saveClosingRegister(): void{
    if(this.cashier){
      this.cashierService.addRegisterClosing(this.cashier).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(response.message, 'top', 3000);
          if(response){
            sessionStorage.removeItem('isOpen');
          }
        },
        error: (error) => {
          const message = error.error?.error || error.error?.message || error?.error;
          console.log('Error: ', message);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  validateAmounts(): boolean {
    return this.cashier.amountClosingCash <= 0 || this.cashier.amountClosingPos <= 0;
  }

  resetValue(): void{
    this.cashier.amountClosingCash = 0;
    this.cashier.amountClosingPos = 0;
    this.cashier.comment = '';
  }

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
