import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Dispatch } from '../../../models/entity/sale/dispatch';
import { CustomerService } from '../../../services/customer/customer.service';
import { CustomerResponse } from '../../../models/entity/customer/customer-response';
import { SaleService } from '../../../services/sale/sale.service';
import { PaymentMethod } from '../../../models/enum/payment-method';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';
import { DispatchHelperService } from '../../../services/sale/dispatch-helper.service';
import { SaleRequest } from '../../../models/entity/sale/sale-request';
import { SaleHelperService } from '../../../services/sale/sale-helper.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { SaleMapperService } from '../../../mapper/sale-mapper.service';

@Component({
  selector: 'app-payment-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-dispatch.component.html',
  styleUrl: './payment-dispatch.component.css'
})
export class PaymentDispatchComponent implements OnInit, OnChanges {

  @Input() selectedDispatchPayment: Dispatch;
  @Input() totalSum: number = 0;
  @Output() dispatchPaid = new EventEmitter<boolean>();
  saleRequest: SaleRequest;
  customerList: CustomerResponse[] = [];
  totalSale: number = 0;
  cashAmount: number = 0;
  isDiscount: boolean = false;
  isCash: boolean = false;
  isTransfer: boolean = false;
  isCredit: boolean = false;
  isDebit: boolean = false;
  isMix: boolean = false;
  isLoan: boolean = false;
  isOk: boolean = false;
  showSwitch: boolean = false;
  showComment: boolean = false;

  constructor(
    private customerService: CustomerService,
    private saleService: SaleService,
    private mapper: SaleMapperService,
    private notification: NotificationService,
    protected permission: DispatchPermissionsService,
    protected helper: DispatchHelperService,
    private saleHelper: SaleHelperService,
    private errorMessage: ErrorMessageService,
    public modalService: ModalService) {
    this.selectedDispatchPayment = helper.initializeDispatch();
    this.saleRequest = this.saleHelper.initializeSaleRequest();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalSum']) {
      this.totalSale = this.totalSum;
    }
  }

  ngOnInit(): void {
    this.modalService.openModal();
  }

  processDispatchForSale(dispatchSelected: Dispatch): void {
    if (dispatchSelected) {
      this.saleRequest.idDispatch = dispatchSelected.id;
      this.saleRequest.total = this.totalSale;
      this.saleRequest.tax = dispatchSelected.saleDetails.reduce((sum, value) => sum + value.tax, 0);
      this.saleRequest.detailList = this.mapper.mapSaleDetailResponseToRequest(dispatchSelected.saleDetails);
      this.saleService.createSaleFromDispatch(this.saleRequest).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(response.message, 'top', 3000);
          this.dispatchPaid.emit(true);
          this.modalService.closeModal();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      });
    }
  }

  loadCustomersForPaymentProcessing(): void {
    this.customerService.getCustomer().subscribe({
      next: (list) => {
        const searchTerms = this.selectedDispatchPayment?.customer.toLowerCase().split(" ");
        this.customerList = list.filter(customer => 
          customer.account &&
          searchTerms?.some(term => 
            customer.name.toLowerCase().includes(term) || 
            customer.surname.toLowerCase().includes(term)
          )
        );
      }
    });
  }

  initiateMethodPayment(button: number): void {
    switch (button) {
      case 1:
        this.showSwitch = true;
        this.isCash = true;
        this.isTransfer = false;
        this.isLoan = false;
        this.isCredit = false;
        this.isDebit = false;
        this.isMix = false;
        this.isOk = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.EFECTIVO;
        break;
      case 2:
        this.showSwitch = true;
        this.isTransfer = true;
        this.isCash = false;
        this.isLoan = false;
        this.isCredit = false;
        this.isDebit = false;
        this.isMix = false;
        this.isOk = false;
        this.showComment = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.TRANSFERENCIA;
        break;
      case 3:
        this.showSwitch = true;
        this.isLoan = true;
        this.isTransfer = false;
        this.isCash = false;
        this.isCredit = false;
        this.isDebit = false;
        this.isMix = false;
        this.isOk = true;
        this.showComment = false;
        this.saleRequest.paymentMethod = PaymentMethod.PRESTAMO;
        this.loadCustomersForPaymentProcessing();
        break;
      case 4:
        this.showSwitch = true;
        this.isCredit = true;
        this.isTransfer = false;
        this.isLoan = false;
        this.isCash = false;
        this.isDebit = false;
        this.isMix = false;
        this.isOk = false;
        this.showComment = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.CREDITO;
        break;
      case 5:
        this.showSwitch = true;
        this.isDebit = true;
        this.isTransfer = false;
        this.isLoan = false;
        this.isCredit = false;
        this.isCash = false;
        this.isMix = false;
        this.isOk = false;
        this.showComment = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.DEBITO;
        break;
      case 6:
        this.showSwitch = false;
        this.isMix = true;
        this.isTransfer = false;
        this.isLoan = false;
        this.isCredit = false;
        this.isDebit = false;
        this.isCash = false;
        this.isOk = true;
        this.showComment = false;
        this.saleRequest.paymentMethod = PaymentMethod.MIXTO;
        this.loadCustomersForPaymentProcessing();
        break;
    }
  }

  handleDiscountSwitch(): void {
    const totalWithoutDiscount = this.selectedDispatchPayment.saleDetails.reduce(
      (sum, value) => sum + (value.price * value.quantity), 0);

    if (this.isDiscount) {
      if (this.saleRequest.discount > 0 && this.saleRequest.discount < this.totalSale) {
        this.totalSale = Math.max(0, totalWithoutDiscount - this.saleRequest.discount);
      }
    } else {
      this.saleRequest.discount = 0;
      this.totalSale = totalWithoutDiscount;
    }
  }

  handleCommentToRequest(option: string): void {
    if (this.saleRequest.paymentMethod === PaymentMethod.EFECTIVO || this.saleRequest.paymentMethod === PaymentMethod.MIXTO) {
      this.saleRequest.comment = this.cashAmount.toString();
    }

    if (option.includes('comment')) {
      this.isOk = !!this.saleRequest.comment;
    }
    if (option.includes('cash')) {
      this.isOk = !!this.cashAmount;
    }
  }

  resetModal(): void {
    this.totalSale = 0;
    this.isDiscount = false;
    this.isCash = false;
    this.isMix = false;
    this.isLoan = false;
    this.isOk = false;
    this.showSwitch = false;
    this.saleRequest = this.saleHelper.initializeSaleRequest();
  }
}
