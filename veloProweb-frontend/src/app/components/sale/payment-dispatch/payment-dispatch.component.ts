import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { CustomerService } from '../../../services/customer/customer.service';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { SaleService } from '../../../services/Sale/sale.service';
import { PaymentMethod } from '../../../models/enum/payment-method';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { Role } from '../../../models/enum/role';
import { RoleService } from '../../../services/User/role.service';

@Component({
  selector: 'app-payment-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-dispatch.component.html',
  styleUrl: './payment-dispatch.component.css'
})
export class PaymentDispatchComponent implements OnChanges, OnInit {

  @Input() selectedDispatchPayment: Dispatch | null = null;
  @Input() saleDetailDispatchList: DetailSaleRequestDTO[] = [];
  @Output() dispatchPaid = new EventEmitter<boolean>();
  requestDTO: SaleRequestDTO;
  customerList: Customer[] = [];
  totalSum: number = 0;
  discountAmount: number = 0;
  cashAmount: number = 0;
  comment: string = '';
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
  role = Role;

  constructor(
    private customerService: CustomerService,
    private saleService: SaleService,
    private notification: NotificationService,
    protected roleService: RoleService,
    public modalService: ModalService) {
    this.requestDTO = this.initializeRequestDTO();
  }

  ngOnInit(): void {
    this.modalService.openModal();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['saleDetailDispatchList'] && changes['saleDetailDispatchList'].currentValue) {
      this.totalSum = this.saleDetailDispatchList.reduce((sum, value) => sum + (value.price * value.quantity), 0);
    }
  }

  createSaleFromDispatch(dispatchSelected: Dispatch): void {
    if (dispatchSelected) {
      this.requestDTO.id = dispatchSelected.id;
      this.requestDTO.discount = this.discountAmount;
      this.requestDTO.total = this.totalSum;
      this.requestDTO.detailList = dispatchSelected.detailSaleDTOS ? [...dispatchSelected.detailSaleDTOS] : [];

      this.saleService.createSaleFromDispatch(this.requestDTO).subscribe({
        next: (response) => {
          const message = response.message
          this.notification.showSuccessToast(message, 'top', 3000);
          this.dispatchPaid.emit(true);
          this.modalService.closeModal();
        }, error: (error) => {
          const message = error.error?.error || error.error?.message || error?.error;
          console.log("ERROR: ", message);
          this.notification.showErrorToast(message, 'top', 3000);
        }
      })
    }
  }

  getCustomersToDispatchPayment(): void {
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
      }, 
      error: (error) => {
        console.log('Error al obtener los clientes, ', error?.error);
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
        this.requestDTO.idCustomer = null;
        this.requestDTO.paymentMethod = PaymentMethod.EFECTIVO;
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
        this.requestDTO.idCustomer = null;
        this.requestDTO.paymentMethod = PaymentMethod.TRANSFERENCIA;
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
        this.requestDTO.paymentMethod = PaymentMethod.PRESTAMO;
        this.getCustomersToDispatchPayment();
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
        this.requestDTO.idCustomer = null;
        this.requestDTO.paymentMethod = PaymentMethod.CREDITO;
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
        this.requestDTO.idCustomer = null;
        this.requestDTO.paymentMethod = PaymentMethod.DEBITO;
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
        this.requestDTO.paymentMethod = PaymentMethod.MIXTO;
        this.getCustomersToDispatchPayment();
        break;
    }
  }

  handleDiscountSwitch(): void {
    const totalWithoutDiscount = this.saleDetailDispatchList.reduce((sum, value) => sum + (value.price * value.quantity), 0);

    if (this.isDiscount) {
      if (this.discountAmount > 0 && this.discountAmount < this.totalSum) {
        this.totalSum = Math.max(0, totalWithoutDiscount - this.discountAmount);
      }
    } else {
      this.discountAmount = 0;
      this.totalSum = totalWithoutDiscount;
    }
  }

  handleCommentToRequest(option: string): void {
    if (this.requestDTO.paymentMethod === PaymentMethod.CREDITO || this.requestDTO.paymentMethod === PaymentMethod.DEBITO
      || this.requestDTO.paymentMethod === PaymentMethod.TRANSFERENCIA) {
      this.requestDTO.comment = this.comment;
    } else if (this.requestDTO.paymentMethod === PaymentMethod.EFECTIVO || this.requestDTO.paymentMethod === PaymentMethod.MIXTO) {
      this.requestDTO.comment = this.cashAmount.toString();
    }

    if (option.includes('comment')) {
      this.isOk = !!this.comment;
    }
    if (option.includes('cash')) {
      this.isOk = !!this.cashAmount;
    }
  }

  resetModal(): void {
    this.selectedDispatchPayment = null;
    this.totalSum = 0;
    this.isDiscount = false;
    this.isCash = false;
    this.isMix = false;
    this.isLoan = false;
    this.isOk = false;
    this.discountAmount = 0;
    this.showSwitch = false;
    this.requestDTO = this.initializeRequestDTO();
  }

  initializeRequestDTO(): SaleRequestDTO {
    return {
      id: 0,
      date: '',
      idCustomer: null,
      paymentMethod: PaymentMethod.EFECTIVO,
      tax: 0,
      total: 0,
      discount: 0,
      numberDocument: 0,
      comment: '',
      detailList: [],
    }
  }
}
