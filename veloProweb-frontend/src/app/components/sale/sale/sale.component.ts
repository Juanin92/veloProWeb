import { AfterViewInit, Component, OnInit } from '@angular/core';
import { TooltipService } from '../../../utils/tooltip.service';
import { ProductListComponent } from "../../product/productList/product-list.component";
import { CustomerResponse } from '../../../models/entity/customer/customer-response';
import { SaleDetail } from '../../../models/entity/sale/sale-detail';
import { NotificationService } from '../../../utils/notification-service.service';
import { SaleHelperService } from '../../../services/sale/sale-helper.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../../services/customer/customer.service';
import { SaleService } from '../../../services/sale/sale.service';
import { PaymentMethod } from '../../../models/enum/payment-method';
import { DispatchModalComponent } from "../dispatch-modal/dispatch-modal.component";
import { SalePermissionsService } from '../../../services/permissions/sale-permissions.service';
import { CashRegisterService } from '../../../services/sale/cash-register.service';
import { ProductResponse } from '../../../models/entity/product/product-response';
import { SaleRequest } from '../../../models/entity/sale/sale-request';
import { SaleMapperService } from '../../../mapper/sale-mapper.service';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-sale',
  standalone: true,
  imports: [FormsModule, CommonModule, ProductListComponent, DispatchModalComponent],
  templateUrl: './sale.component.html',
  styleUrl: './sale.component.css'
})
export class SaleComponent implements AfterViewInit, OnInit {

  customerList: CustomerResponse[] = [];
  productSelected: ProductResponse | null = null;
  saleDetailList: SaleDetail[] = [];
  saleRequest: SaleRequest;
  AccumulatedSaleTotal: number = 0; //Total de ventas registradas en la BD
  cashAmount: number = 0;
  changeAmount: number = 0;
  originalTotal: number = 0;
  showSwitch: boolean =  false;
  hasDiscount: boolean = false; 
  isPaymentInCash: boolean = false; 
  isPaymentByTransfer: boolean =  false;
  isPaymentOnLoan: boolean =  false;
  isPaymentByCredit: boolean =  false;
  isPaymentByDebitCard: boolean =  false;
  hasMixedPayment: boolean =  false;
  isTransactionValid: boolean = false;
  isRegisterOpen: boolean = false;
  editingFields: { [key: string]: { quantity?: boolean; } } = {}; // (Map) Campos de edición activa para cantidades o precios en detalles de compra


  constructor(
    private saleService: SaleService,
    private customerService: CustomerService,
    private tooltipService: TooltipService,
    private notification: NotificationService,
    private helper: SaleHelperService,
    private mapper: SaleMapperService,
    protected permission: SalePermissionsService,
    private errorMessage: ErrorMessageService,
    private cashRegisterService: CashRegisterService) {
    this.saleRequest = helper.initializeSaleRequest();
  }

  ngOnInit(): void {
    this.loadCustomers();
    this.loadTotalSales();
    this.hasOpeningCashierRegister();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  /**Obtener una lista de clientes */
  loadCustomers(): void {
    this.customerService.getCustomer().subscribe((list) => this.customerList = list);
  }

  /**
   * Obtiene número de total de ventas registradas
   */
  loadTotalSales(): void{
    this.saleService.getTotalSale().subscribe((totalSales) => this.AccumulatedSaleTotal = totalSales);
  }

  /**
   * Crea un nuevo proceso de venta, 
   * confirmando el método de pago y enviando la solicitud al servicio de ventas.
   */
  async createNewSaleProcess(): Promise<void>{
    await this.requestPaymentConfirmation(this.saleRequest.paymentMethod!);
    if (this.saleRequest.detailList.length > 0 || this.saleRequest.paymentMethod !== null) {
      this.saleRequest.detailList = this.mapper.mapToSaleDetailRequest(this.saleDetailList);
      this.saleService.createSale(this.saleRequest).subscribe({
        next: (response) =>{
          this.notification.showSuccessToast(`N°${this.AccumulatedSaleTotal}, ${response.message}`, 'top', 3000);
          this.resetProcess();
          this.loadTotalSales();
        }, error: (error) =>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`Error al realizar la venta \n${message}`, 'top', 5000);
        }
      });
    } else {
      this.notification.showWarning('Problemas con la venta', 'Por favor, Ingrese datos a la venta.');
    }
  }

  /**
     * Agrega un producto seleccionado a la lista de detalles de venta
     * calcula los valores de impuesto y precio total del detalle
     * @param product - Producto seleccionado para agregar a la lista
     */
  addSelectedProductToSaleDetailList(product: ProductResponse): void {
    this.productSelected = product;
    const isProductAdded = this.saleDetailList.some((detail) => detail.id === product.id);
    if (!isProductAdded) {
      const newSaleDetail: SaleDetail = {
        id: product.id,
        quantity: 1,
        price: 0,
        tax: 0,
        total: 0,
        product: product,
      }
      newSaleDetail.price = this.productSelected.salePrice * 1.19;
      newSaleDetail.tax = this.productSelected.salePrice * 0.19;
      newSaleDetail.total = newSaleDetail.price * newSaleDetail.quantity;
      this.saleDetailList.push(newSaleDetail);
      this.sumTotalAndTaxList();
    } else {
      this.notification.showErrorToast('Producto ya agregado!', 'center', 1500);
    }
  }

  /**
   * Inicia el método de pago según el botón presionado.
   * @param button - Número del botón que indica el método de pago
   */
  initiateMethodPayment(button: number): void{
    switch(button){
      case 1 : 
        this.showSwitch = true;
        this.isPaymentInCash = true;
        this.isPaymentByTransfer = false;
        this.isPaymentOnLoan = false;
        this.isPaymentByCredit = false;
        this.isPaymentByDebitCard = false;
        this.hasMixedPayment = false;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.EFECTIVO;
        break;
      case 2 : 
        this.showSwitch = true;
        this.isPaymentByTransfer = true;
        this.isPaymentInCash = false;
        this.isPaymentOnLoan = false;
        this.isPaymentByCredit = false;
        this.isPaymentByDebitCard = false;
        this.hasMixedPayment = false;
        this.isTransactionValid = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.TRANSFERENCIA;
        break;
      case 3 : 
        this.showSwitch = true;
        this.isPaymentOnLoan = true;
        this.isPaymentByTransfer = false;
        this.isPaymentInCash = false;
        this.isPaymentByCredit = false;
        this.isPaymentByDebitCard = false;
        this.hasMixedPayment = false;
        this.isTransactionValid = false;
        this.saleRequest.paymentMethod = PaymentMethod.PRESTAMO;
        break;
      case 4 : 
        this.showSwitch = true;
        this.isPaymentByCredit = true;
        this.isPaymentByTransfer = false;
        this.isPaymentOnLoan = false;
        this.isPaymentInCash = false;
        this.isPaymentByDebitCard = false;
        this.hasMixedPayment = false;
        this.isTransactionValid = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.CREDITO;
        break;
      case 5 : 
        this.showSwitch = true;
        this.isPaymentByDebitCard = true;
        this.isPaymentByTransfer = false;
        this.isPaymentOnLoan = false;
        this.isPaymentByCredit = false;
        this.isPaymentInCash = false;
        this.hasMixedPayment = false;
        this.isTransactionValid = true;
        this.saleRequest.idCustomer = 0;
        this.saleRequest.paymentMethod = PaymentMethod.DEBITO;
        break;
      case 6 :
        this.showSwitch = false;
        this.hasMixedPayment = true;
        this.isPaymentByTransfer = false;
        this.isPaymentOnLoan = false;
        this.isPaymentByCredit = false;
        this.isPaymentByDebitCard = false;
        this.isPaymentInCash = false;
        this.isTransactionValid = false;
        this.saleRequest.paymentMethod = PaymentMethod.MIXTO;
        break;
    }
  }

  /**
   * Solicita confirmación del método de pago.
   * @param method - Método de pago seleccionado
   * @returns - promesa que se resuelve si se confirma el pago, o se rechaza si se cancela
   */
  requestPaymentConfirmation(method: PaymentMethod): Promise<void> {
    return new Promise((resolve, reject) => {
      if (method === PaymentMethod.CREDITO || method === PaymentMethod.DEBITO) {
        this.notification.showInputDialog(
          'Ingrese comprobante para confirmar la venta',
          'N° Comprobante',
          'Aceptar',
          'Cancelar'
        ).then((result) => {
          if (result.isConfirmed) {
            this.saleRequest.comment = result.value;
            resolve();
          } else {
            this.resetProcess();
            this.notification.showError('Operación Cancelada', 'Ha cancelado la operación.');
            reject('Cancelado por el usuario');
          }
        });
      } else if (method === PaymentMethod.TRANSFERENCIA) {
        this.notification.showInputDialog(
          'Ingrese transferencia para confirmar la venta',
          'N° Transferencia',
          'Aceptar',
          'Cancelar'
        ).then((result) => {
          if (result.isConfirmed) {
            this.saleRequest.comment = result.value;
            resolve();
          } else {
            this.resetProcess();
            this.notification.showError('Operación Cancelada', 'Ha cancelado la operación.');
            reject('Cancelado por el usuario');
          }
        });
      } else if (method === PaymentMethod.EFECTIVO) {
        this.saleRequest.comment = this.cashAmount.toString();
        resolve();
      } else if ( method === PaymentMethod.PRESTAMO){
        resolve();
      } else if(method === PaymentMethod.MIXTO) {
        this.saleRequest.comment = this.cashAmount.toString();
        resolve();
      } else {
        reject('Método de pago no reconocido');
      }
    });
  }

  /**
   * Elimina un producto de la lista de detalle
   * @param index - Identificador del detalle en la lista
   */
  removeDetail(index: number): void {
    this.saleDetailList.splice(index, 1);
    this.sumTotalAndTaxList();
  }

  /**
     * Actualiza los totales de un detalle de venta al modificar la cantidad a vender
     * @param saleDetail - Detalle al cual debe actualizar
     */
  updateTotal(saleDetail: SaleDetail): void {
    if (saleDetail.quantity <= 0) {
      saleDetail.quantity = 1;
      saleDetail.price = saleDetail.price;
      saleDetail.total = saleDetail.total;
    } else {
      saleDetail.total = saleDetail.price * saleDetail.quantity;
    }
    this.sumTotalAndTaxList();
  }

  /** Calcula el total y los impuestos acumulados de la lista de detalles */
  sumTotalAndTaxList(): void {
    this.saleRequest.tax = 0;
    this.saleRequest.total = 0;
    this.saleDetailList.forEach((saleDetail) => {
      this.saleRequest.total += saleDetail.total;
      this.saleRequest.tax += saleDetail.tax * saleDetail.quantity;
    });
    this.originalTotal = this.saleRequest.total ;
  }

  /**
   * Verifica si se ha elegido un cliente para el pago.
   * Permite el pago de la venta
   */
  confirmCustomerForPayment(): void{
    if (this.saleRequest.idCustomer) {
      this.isTransactionValid = true;
    }
  }

  /**
   * Actualiza el total cuando se paga en efectivo.
   */
  updateTotalWithCash(): void{
    if(this.hasMixedPayment){
      if(this.cashAmount < this.saleRequest.total ){
        this.changeAmount = this.saleRequest.total - this.cashAmount;
        this.saleRequest.total = this.changeAmount;
      }else if(this.cashAmount === 0){
        this.changeAmount = 0;
      }else{
        this.cashAmount = 0;
        this.notification.showError("Monto erróneo", `Debe ingresar un monto inferior a $${this.saleRequest.total } 
          para hacer un abono a la venta`);
      }
    }
    if(this.isPaymentInCash){
      if(this.cashAmount >= this.saleRequest.total ){
        this.changeAmount = this.cashAmount - this.saleRequest.total ;
        this.isTransactionValid = true;
      }else if(this.cashAmount === 0){
        this.changeAmount = 0;
      }else{
        this.cashAmount = 0;
        this.notification.showError("Monto erróneo", `Debe ingresar un monto igual o superior a $${this.saleRequest.total }`);
      }
    }
  }

  /**
   * Actualiza el total con un descuento aplicado.
   */
  updateTotalWithDiscount(): void{
    if(!this.hasMixedPayment){
      if (this.saleRequest.discount >= 0 && this.saleRequest.discount < this.originalTotal) {
        this.saleRequest.total  = this.originalTotal - this.saleRequest.discount; 
        this.updateTotalWithCash();
      } else {
        this.saleRequest.discount = 0;
        this.notification.showError("Monto erróneo", `Debe agregar un descuento menor al total de la venta o menor a 0`);
      }
    }
  }

  /**
   * Maneja el cambio de estado del descuento.
   */
  handleDiscountSwitch(): void{
    if (this.hasDiscount) {
      this.updateTotalWithDiscount();
    } else {
      this.saleRequest.total  = this.originalTotal;
      this.saleRequest.discount = 0;
      this.updateTotalWithCash();
    }
  }

  hasOpeningCashierRegister(): void {
    const isOpen = sessionStorage.getItem('isOpen');
    if (isOpen) {
        this.isRegisterOpen = true;
    } else {
        this.cashRegisterService.hasOpenRegisterOnDate().subscribe({
            next: (response) => this.isRegisterOpen = response,
            error: () => this.isRegisterOpen = false
        });
    }
  }
  

  /**
   * Reinicia el proceso de venta a su estado inicial.
   */
  resetProcess(): void{
    this.loadTotalSales();
    this.saleRequest = this.helper.initializeSaleRequest();
    this.customerList = [];
    this.saleDetailList = [];
    this.cashAmount = 0;
    this.changeAmount = 0;
    this.showSwitch =  false;
    this.hasDiscount = false;
    this.isPaymentInCash = false;
    this.isPaymentByTransfer = false;
    this.isPaymentOnLoan = false;
    this.isPaymentByCredit = false;
    this.isPaymentByDebitCard = false;
    this.hasMixedPayment = false;
    this.isTransactionValid = false;
    this.loadCustomers();
  }

  /** Habilita la edición de un campo en un detalle específico */
  enableEdit(detail: SaleDetail): void {
    const key = detail.id.toString(); // Usa el ID del detalle como clave
    if (!this.editingFields[key]) {
      this.editingFields[key] = {};
    }
    this.editingFields[key]['quantity'] = true;
  }

  /** Deshabilita la edición de un campo en un detalle específico */
  disableEdit(detail: SaleDetail): void {
    const key = detail.id.toString();
    if (this.editingFields[key]) {
      this.editingFields[key]['quantity'] = false;
    }
    this.updateTotal(detail); // Actualiza el total cuando se desactiva la edición
  }

  /** Verifica si un campo específico está en edición */
  isEditing(detail: SaleDetail): boolean {
    const key = detail.id.toString();
    return !!this.editingFields[key]?.['quantity'];
  }
}
