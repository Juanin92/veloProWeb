import { AfterViewInit, Component, OnInit } from '@angular/core';
import { TooltipService } from '../../../utils/tooltip.service';
import { ProductListComponent } from "../../product/productList/product-list.component";
import { Product } from '../../../models/Entity/Product/product.model';
import { Sale } from '../../../models/Entity/Sale/sale';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { SaleDetail } from '../../../models/Entity/Sale/sale-detail';
import { NotificationService } from '../../../utils/notification-service.service';
import { SaleHelperService } from '../../../services/Sale/sale-helper.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../../services/customer/customer.service';
import { SaleService } from '../../../services/Sale/sale.service';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { PaymentMethod } from '../../../models/enum/payment-method';

@Component({
  selector: 'app-sale',
  standalone: true,
  imports: [FormsModule, CommonModule, ProductListComponent],
  templateUrl: './sale.component.html',
  styleUrl: './sale.component.css'
})
export class SaleComponent implements AfterViewInit, OnInit {

  sale: Sale;
  customerList: Customer[] = [];
  productSelected: Product | null = null;
  saleDetailList: SaleDetail[] = [];
  requestDTO: SaleRequestDTO | null = null;
  total: number = 0; //Total acumulado de la venta
  TotalSaleDB: number = 0; //Total de ventas registradas en la BD
  cashAmount: number = 0;
  changeAmount: number = 0;
  discountAmount: number = 0;
  originalTotal: number = 0;
  showSwitch: boolean =  false;
  isDiscount: boolean = false;
  isCashPayment: boolean = false;
  isTransferPayment: boolean =  false;
  isLoanPayment: boolean =  false;
  isCreditPayment: boolean =  false;
  isDebitPayment: boolean =  false;
  isMixPayment: boolean =  false;
  isOk: boolean = false;
  editingFields: { [key: string]: { quantity?: boolean; } } = {}; // (Map) Campos de edición activa para cantidades o precios en detalles de compra


  constructor(
    private saleService: SaleService,
    private customerService: CustomerService,
    private tooltipService: TooltipService,
    private notification: NotificationService,
    private helper: SaleHelperService) {
    this.sale = helper.createEmptySale();
  }

  ngOnInit(): void {
    this.getCustomer();
    this.getTotalSale();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  async createNewSaleProcess(): Promise<void>{
    await this.requestPaymentConfirmation(this.sale.paymentMethod!);
    this.requestDTO = this.helper.createDto(this.sale, this.saleDetailList, 
      this.TotalSaleDB, this.total, this.discountAmount);
    if (this.requestDTO.detailList.length > 0 || this.requestDTO.paymentMethod !== null) {
      this.saleService.createSale(this.requestDTO).subscribe((response) => {
        console.log('Venta realizada exitosamente: ', response);
        this.notification.showSuccessToast(`¡Venta N°${this.TotalSaleDB} fue realizada exitosamente!`, 'top', 3000);
        this.resetProcess();
        this.getTotalSale();
      }, (error) =>{
        const message = error.error?.message || error.error?.error;
        console.error('Error al realizar la venta: ', error);
        this.notification.showErrorToast(`Error al realizar la venta \n${message}`, 'top', 5000);
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
  addSelectedProductToSaleDetailList(product: Product): void {
    this.productSelected = product;
    const isProductAdded = this.saleDetailList.some((detail) => detail.product.id === product.id);
    if (!isProductAdded) {
      const newSaleDetail: SaleDetail = {
        id: 0,
        quantity: 1,
        price: 0,
        tax: 0,
        total: 0,
        sale: this.sale,
        product: product
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

  initiateMethodPayment(button: number): void{
    switch(button){
      case 1 : 
        this.showSwitch = true;
        this.isCashPayment = true;
        this.isTransferPayment = false;
        this.isLoanPayment = false;
        this.isCreditPayment = false;
        this.isDebitPayment = false;
        this.isMixPayment = false;
        this.sale.customer = null;
        this.sale.paymentMethod = PaymentMethod.EFECTIVO;
        break;
      case 2 : 
        this.showSwitch = true;
        this.isTransferPayment = true;
        this.isCashPayment = false;
        this.isLoanPayment = false;
        this.isCreditPayment = false;
        this.isDebitPayment = false;
        this.isMixPayment = false;
        this.isOk = true;
        this.sale.customer = null;
        this.sale.paymentMethod = PaymentMethod.TRANSFERENCIA;
        break;
      case 3 : 
        this.showSwitch = true;
        this.isLoanPayment = true;
        this.isTransferPayment = false;
        this.isCashPayment = false;
        this.isCreditPayment = false;
        this.isDebitPayment = false;
        this.isMixPayment = false;
        this.isOk = false;
        this.sale.paymentMethod = PaymentMethod.PRESTAMO;
        break;
      case 4 : 
        this.showSwitch = true;
        this.isCreditPayment = true;
        this.isTransferPayment = false;
        this.isLoanPayment = false;
        this.isCashPayment = false;
        this.isDebitPayment = false;
        this.isMixPayment = false;
        this.isOk = true;
        this.sale.customer = null;
        this.sale.paymentMethod = PaymentMethod.CREDITO;
        break;
      case 5 : 
        this.showSwitch = true;
        this.isDebitPayment = true;
        this.isTransferPayment = false;
        this.isLoanPayment = false;
        this.isCreditPayment = false;
        this.isCashPayment = false;
        this.isMixPayment = false;
        this.isOk = true;
        this.sale.customer = null;
        this.sale.paymentMethod = PaymentMethod.DEBITO;
        break;
      case 6 :
        this.showSwitch = false;
        this.isMixPayment = true;
        this.isTransferPayment = false;
        this.isLoanPayment = false;
        this.isCreditPayment = false;
        this.isDebitPayment = false;
        this.isCashPayment = false;
        this.isOk = false;
        this.sale.paymentMethod = PaymentMethod.MIXTO;
        break;
    }
  }

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
            this.sale.comment = result.value;
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
            this.sale.comment = result.value;
            resolve();
          } else {
            this.resetProcess();
            this.notification.showError('Operación Cancelada', 'Ha cancelado la operación.');
            reject('Cancelado por el usuario');
          }
        });
      } else if (method === PaymentMethod.EFECTIVO) {
        this.sale.comment = this.cashAmount.toString();
        resolve();
      } else if (method === PaymentMethod.MIXTO || method === PaymentMethod.PRESTAMO){
        resolve();
      } else {
        reject('Método de pago no reconocido');
      }
    });
  }
  
  /**Obtener una lista de clientes */
  getCustomer(): void {
    this.customerService.getCustomer().subscribe((list) => {
      this.customerList = list;
    });
  }

  getTotalSale(): void{
    this.saleService.getTotalSale().subscribe((totalSales) =>{
      this.TotalSaleDB = totalSales;
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
    this.total = 0;
    this.sale.tax = 0;
    this.saleDetailList.forEach((saleDetail) => {
      this.total += saleDetail.total;
      this.sale.tax += saleDetail.tax * saleDetail.quantity;
    });
    this.originalTotal = this.total;
  }

  chooseCustomerToPay(): void{
    if (this.sale.customer) {
      this.isOk = true;
    }
  }

  updateTotalWithCash(): void{
    if(this.cashAmount >= this.total){
      this.changeAmount = this.cashAmount - this.total;
      this.isOk = true;
    }else if(this.cashAmount === 0){
      this.changeAmount = 0;
    }else{
      this.cashAmount = 0;
      this.notification.showError("Monto erróneo", `Debe ingresar un monto igual o superior a $${this.total}`);
    }
  }

  updateTotalWithDiscount(): void{
    if (this.discountAmount >= 0 && this.discountAmount < this.originalTotal) {
      this.total = this.originalTotal - this.discountAmount; 
      this.updateTotalWithCash();
    } else {
      this.discountAmount = 0;
      this.notification.showError("Monto erróneo", `Debe agregar un descuento menor al total de la venta o menor a 0`);
    }
  }

  handleDiscountSwitch(): void{
    if (this.isDiscount) {
      this.updateTotalWithDiscount();
    } else {
      this.total = this.originalTotal;
      this.discountAmount = 0;
      this.updateTotalWithCash();
    }
  }

  resetProcess(): void{
    this.getTotalSale();
    this.sale = this.helper.createEmptySale();
    this.customerList = [];
    this.saleDetailList = [];
    this.total = 0;
    this.cashAmount = 0;
    this.changeAmount = 0;
    this.discountAmount = 0;
    this.showSwitch =  false;
    this.isDiscount = false;
    this.isCashPayment = false;
    this.isTransferPayment = false;
    this.isLoanPayment = false;
    this.isCreditPayment = false;
    this.isDebitPayment = false;
    this.isMixPayment = false;
    this.isOk = false;
    this.getCustomer();
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
