import { Component } from '@angular/core';
import { CashRegisterService } from '../../../services/Sale/cash-register.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { CashRegisterForm } from '../../../models/Entity/Sale/cash-register-form';

@Component({
  selector: 'app-cashier',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cashier.component.html',
  styleUrl: './cashier.component.css'
})
export class CashierComponent {

  cashier: CashRegisterForm;

  constructor(
    private cashierService: CashRegisterService,
    private router: Router,
    private notification: NotificationService,
    private errorMessage: ErrorMessageService,
    public modalService: ModalService){
      this.cashier = this.initializeCashier();
    }

  createCashierOpening(): void {
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
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  closeCashierRegister(): void{
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
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  isAmountsValid(): boolean {
    return this.cashier.amountClosingCash <= 0 || this.cashier.amountClosingPos <= 0;
  }

  initializeCashierValues(): void{
    this.cashier.amountClosingCash = 0;
    this.cashier.amountClosingPos = 0;
    this.cashier.comment = '';
  }

  initializeCashier(): CashRegisterForm{
    return {
      id: 0,
      amountOpening: 0,
      amountClosingCash: 0,
      amountClosingPos: 0,
      comment: '',
    }
  }
}
