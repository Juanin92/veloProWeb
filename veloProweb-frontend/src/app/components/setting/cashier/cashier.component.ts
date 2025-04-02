import { Component } from '@angular/core';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { CashRegister } from '../../../models/Entity/cash-register';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';

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
    private notification: NotificationService,
    public modalService: ModalService){}

  saveOpeningRegister(): void {
    if (this.cashier.amountOpening !== null) {
      this.cashierService.addRegisterOpening(this.cashier.amountOpening).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(response.message, 'top', 3000);
          sessionStorage.setItem('isOpen', true.toString());
          this.modalService.closeModal();
          setTimeout(() => {
            window.location.reload();
          }, 1000);
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
          sessionStorage.removeItem('isOpen');
          this.modalService.closeModal();
          setTimeout(() => {
            window.location.reload();
          }, 4000);
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
