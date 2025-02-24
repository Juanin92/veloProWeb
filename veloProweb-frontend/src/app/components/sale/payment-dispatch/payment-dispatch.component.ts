import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { CustomerService } from '../../../services/customer/customer.service';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { SaleService } from '../../../services/Sale/sale.service';

@Component({
  selector: 'app-payment-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-dispatch.component.html',
  styleUrl: './payment-dispatch.component.css'
})
export class PaymentDispatchComponent implements OnChanges, OnInit{

  @Input() selectedDispatchPayment: Dispatch | null = null;
  @Input() saleDetailDispatchList: DetailSaleRequestDTO[] = [];
  requestDTO: SaleRequestDTO | null = null;
  customerList: Customer[] = [];
  totalSum: number = 0;
  discountAmount: number = 0;
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
  dispatch: Dispatch = {
    id: 0,
    trackingNumber: '',
    status: '',
    address: '',
    comment: '',
    customer: '',
    hasSale: false,
    created: '',
    deliveryDate: '',
    detailSaleDTOS: null,
  }

  constructor(
    private dispatchService: DispatchService, 
    private customerService: CustomerService,
    private saleService: SaleService){}

  ngOnInit(): void {
    this.getCustomersToDispatchPayment();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['saleDetailDispatchList'] && changes['saleDetailDispatchList'].currentValue) {
      this.totalSum = this.saleDetailDispatchList.reduce((sum, value) => sum + (value.price * value.quantity), 0);
    }
  }

  getCustomersToDispatchPayment(): void{
    this.customerService.getCustomer().subscribe({
      next:(list)=>{
        this.customerList = list.filter(customer => customer.account);
      }, error: (error)=>{
        console.log('Error al obtener los clientes, ', error?.error);
      }
    })
  }

  handleStatusDispatch(dispatch: Dispatch, action: number): void{
    this.dispatchService.handleStatusDispatch(dispatch.id, action).subscribe({
      next:(response)=>{
        const message = response.message;
        console.log('Cambio de estado del despacho, ', message);
        // this.getDispatches();
      },error:(error)=>{
        const message = error.error?.message || error.error?.error || error?.error;
        console.log('Error estado del despacho, ', message);
      }
    });
  }

  initiateMethodPayment(button: number): void{
      switch(button){
        case 1 : 
          this.showSwitch = true;
          this.isCash = true;
          this.isTransfer = false;
          this.isLoan = false;
          this.isCredit = false;
          this.isDebit = false;
          this.isMix = false;
          // this.sale.customer = null;
          // this.sale.paymentMethod = PaymentMethod.EFECTIVO;
          break;
        case 2 : 
          this.showSwitch = true;
          this.isTransfer = true;
          this.isCash = false;
          this.isLoan = false;
          this.isCredit = false;
          this.isDebit = false;
          this.isMix = false;
          this.isOk = true;
          this.showComment = true;
          // this.sale.customer = null;
          // this.sale.paymentMethod = PaymentMethod.TRANSFERENCIA;
          break;
        case 3 : 
          this.showSwitch = true;
          this.isLoan = true;
          this.isTransfer = false;
          this.isCash = false;
          this.isCredit = false;
          this.isDebit = false;
          this.isMix = false;
          this.isOk = false;
          this.showComment = false;
          // this.sale.paymentMethod = PaymentMethod.PRESTAMO;
          break;
        case 4 : 
          this.showSwitch = true;
          this.isCredit = true;
          this.isTransfer = false;
          this.isLoan = false;
          this.isCash = false;
          this.isDebit = false;
          this.isMix = false;
          this.isOk = true;
          this.showComment = true;
          // this.sale.customer = null;
          // this.sale.paymentMethod = PaymentMethod.CREDITO;
          break;
        case 5 : 
          this.showSwitch = true;
          this.isDebit = true;
          this.isTransfer = false;
          this.isLoan = false;
          this.isCredit = false;
          this.isCash = false;
          this.isMix = false;
          this.isOk = true;
          this.showComment = true;
          // this.sale.customer = null;
          // this.sale.paymentMethod = PaymentMethod.DEBITO;
          break;
        case 6 :
          this.showSwitch = false;
          this.isMix = true;
          this.isTransfer = false;
          this.isLoan = false;
          this.isCredit = false;
          this.isDebit = false;
          this.isCash = false;
          this.isOk = false;
          this.showComment = false;
          // this.sale.paymentMethod = PaymentMethod.MIXTO;
          break;
      }
    }

  handleDiscountSwitch(): void {
    const totalWithoutDiscount = this.saleDetailDispatchList.reduce((sum, value) => sum + (value.price * value.quantity), 0);
    
    if (this.isDiscount) {
      if(this.discountAmount > 0 && this.discountAmount < this.totalSum){
        this.totalSum = Math.max(0, totalWithoutDiscount - this.discountAmount);
      }
    } else {
      this.discountAmount = 0;
      this.totalSum = totalWithoutDiscount;
    }
  }

  resetModal(): void{
    this.selectedDispatchPayment = null;
    this.totalSum = 0;
    this.isDiscount = false;
    this.isCash = false;
    this.isMix = false;
    this.isLoan = false;
    this.isOk = false;
    this.discountAmount = 0;
    this.showSwitch = false;
  }
}
