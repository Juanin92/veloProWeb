import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Purchase } from '../../../models/entity/purchase/purchase';
import { PurchaseValidator } from '../../../validation/purchase-validator';
import { CommonModule } from '@angular/common';
import { SupplierService } from '../../../services/purchase/supplier.service';
import { Supplier } from '../../../models/entity/purchase/supplier';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../models/entity/product/product';
import { TooltipService } from '../../../utils/tooltip.service';
import { PurchaseDetails } from '../../../models/entity/purchase/purchase-details';
import { NotificationService } from '../../../utils/notification-service.service';
import { PurchaseService } from '../../../services/purchase/purchase.service';
import { PurchaseHelperService } from '../../../services/purchase/purchase-helper.service';
import { Router } from '@angular/router';
import { AddCategoriesComponent } from '../../product/add-categories/add-categories.component';
import { AddProductComponent } from "../../product/add-product/add-product.component";
import { ProductListComponent } from "../../product/productList/product-list.component";
import { PurchasePermissionsService } from '../../../services/permissions/purchase-permissions.service';
import { PurchaseRequest } from '../../../models/entity/purchase/purchase-request';
import { PurchaseMapperService } from '../../../mapper/purchase-mapper.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { PurchaseDetailRequest } from '../../../models/entity/purchase/purchase-detail-request';

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule, AddCategoriesComponent, AddProductComponent, ProductListComponent],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.css'
})
export class PurchaseComponent implements OnInit, AfterViewInit{
  
  purchase: Purchase;
  supplierList: Supplier[] = [];
  purchaseDetailList: PurchaseDetails[] = [];
  purchaseDetailsRequest: PurchaseDetailRequest[] = [];
  purchaseRequest: PurchaseRequest;
  validator = PurchaseValidator;
  currentPurchaseTotal: number = 0; //Total acumulado de la compra
  databasePurchaseTotal: number = 0; //Total de compras registradas en la BD
  editingFields: { [key: string]: { quantity?: boolean; price?: boolean } } = {}; // (Map) Campos de edición activa para cantidades o precios en detalles de compra

  constructor(
    private purchaseService: PurchaseService,
    private supplierService: SupplierService,
    private helper: PurchaseHelperService,
    private mapper: PurchaseMapperService,
    private tooltipService: TooltipService,
    private notification: NotificationService,
    private errorMessage: ErrorMessageService,
    private route: Router,
    protected permission: PurchasePermissionsService
  ){
    this.purchase = helper.initializePurchase();
    this.purchaseRequest = helper.initializePurchaseRequest();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  ngOnInit(): void {
    this.loadSuppliers();
    this.loadTotalPurchase();
  }

  /** Obtiene una lista de proveedores */
  loadSuppliers(): void{
    this.supplierService.getSuppliers().subscribe(
      (list) => this.supplierList = list,
    );
  }

  /** Obtiene el numero total de compras realizadas */
  loadTotalPurchase(): void{
    this.purchaseService.getTotalPurchase().subscribe(
      (totalPurchases) => this.databasePurchaseTotal = totalPurchases,
    );
  }

  /** 
   * Crear un nuevo proceso de compra
   * Redirige a la ruta de stock 
   * */
  initiatePurchaseTransaction(): void{
    if (this.validator.validateForm(this.purchase, this.currentPurchaseTotal) || this.purchaseDetailList) {
      this.purchaseDetailsRequest = this.mapper.mapToPurchaseDetailRequest(this.purchaseDetailList);
      this.purchaseRequest = this.mapper.mapToPurchaseRequest(this.purchase, this.purchaseDetailsRequest);
      this.purchaseService.createPurchase(this.purchaseRequest).subscribe({
        next:(response) => {
          this.notification.showSuccessToast(`N°${this.databasePurchaseTotal} ${response.message}`, 'top', 3000);
          this.resetPurchaseState();
          this.route.navigate(['/main/stock']);
        },error: (error) =>{
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`Error: \n${message}`, 'top', 5000);
        }
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Agrega un producto seleccionado a la lista de detalles de compra
   * valida si el producto ya fue agregado antes a la lista
   * calcula los valores de impuesto y precio total del detalle
   * @param product - Producto seleccionado para agregar a la lista
   */
  addProductToPurchaseDetails(product: Product): void{
    const isProductAdded = this.purchaseDetailList.some((detail) => detail.product.id === product.id);
    if (!isProductAdded) {
      const newPurchaseDetail: PurchaseDetails = {
        product: product,
        quantity: 0,
        price: 0,
        tax: 0,
        total: 0,
      }
  
      newPurchaseDetail.tax = newPurchaseDetail.price * 0.19;
      newPurchaseDetail.total = newPurchaseDetail.price + newPurchaseDetail.tax;
      this.purchaseDetailList.push(newPurchaseDetail);
      this.calculateTotalAndTax();
    } else {
      this.notification.showErrorToast('Producto ya agregado!','center',1500);
    }
  }

  /**
   * Elimina un producto de la lista de detalle
   * @param index - Identificador del detalle en la lista
   */
  removeItemFromPurchase(index: number): void {
    this.purchaseDetailList.splice(index, 1);
    this.calculateTotalAndTax();
  }

  /**
   * Actualiza los totales de un detalle de compra
   * @param purchaseDetail - Detalle al cual debe actualizar
   */
  calculateTotalWithTax(purchaseDetail: PurchaseDetails): void {
    purchaseDetail.tax = purchaseDetail.price * 0.19;
    if(purchaseDetail.quantity < 0 || purchaseDetail.price < 0){
      purchaseDetail.quantity = 0;
      purchaseDetail.price = 0;
      purchaseDetail.total = 0;
    }else{
      purchaseDetail.total = purchaseDetail.price * purchaseDetail.quantity + purchaseDetail.tax;
    }
    this.calculateTotalAndTax();
  }

  /** Calcula el total y los impuestos acumulados de la lista de detalles */
  calculateTotalAndTax(): void{
    this.currentPurchaseTotal = 0;
    this.purchase.tax = 0;
    this.purchaseDetailList.forEach((purchaseDetail) =>{
      this.currentPurchaseTotal += purchaseDetail.total;
      this.purchase.tax += purchaseDetail.tax;
    });
  }

  /** Reinicia los datos de la compra */
  resetPurchaseState(): void{
    this.purchase = this.helper.initializePurchase();
    this.purchaseDetailList = [];
    this.currentPurchaseTotal = 0;
  }

  /** Habilita la edición de un campo en un detalle específico */
  enableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.product.id.toString(); // Usa el ID del detalle como clave
    if (!this.editingFields[key]) {
      this.editingFields[key] = {};
    }
    this.editingFields[key][field] = true;
  }
  
  /** Deshabilita la edición de un campo en un detalle específico */
  disableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.product.id.toString();
    if (this.editingFields[key]) {
      this.editingFields[key][field] = false;
    }
    this.calculateTotalWithTax(detail); // Actualiza el total cuando se desactiva la edición
  }
  
  /** Verifica si un campo específico está en edición */
  isEditing(detail: PurchaseDetails, field: 'quantity' | 'price'): boolean {
    const key = detail.product.id.toString();
    return !!this.editingFields[key]?.[field];
  }
}
