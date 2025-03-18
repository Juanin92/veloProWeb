import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Purchase } from '../../../models/Entity/Purchase/purchase';
import { PurchaseValidator } from '../../../validation/purchase-validator';
import { CommonModule } from '@angular/common';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../models/Entity/Product/product.model';
import { TooltipService } from '../../../utils/tooltip.service';
import { PurchaseDetails } from '../../../models/Entity/Purchase/purchase-details';
import { NotificationService } from '../../../utils/notification-service.service';
import { PurchaseService } from '../../../services/Purchase/purchase.service';
import { PurchaseHelperService } from '../../../services/Purchase/purchase-helper.service';
import { PurchaseRequestDTO } from '../../../models/DTO/purchase-request-dto';
import { Router } from '@angular/router';
import { AddCategoriesComponent } from '../../product/add-categories/add-categories.component';
import { AddProductComponent } from "../../product/add-product/add-product.component";
import { ProductListComponent } from "../../product/productList/product-list.component";
import { PurchasePermissionsService } from '../../../services/Permissions/purchase-permissions.service';

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
  productSelected: Product | null = null;
  purchaseDetailList: PurchaseDetails[] = []; 
  requestDTO: PurchaseRequestDTO | null = null;
  validator = PurchaseValidator;
  total: number = 0; //Total acumulado de la compra
  TotalPurchaseDB: number = 0; //Total de compras registradas en la BD
  editingFields: { [key: string]: { quantity?: boolean; price?: boolean } } = {}; // (Map) Campos de edición activa para cantidades o precios en detalles de compra

  constructor(
    private purchaseService: PurchaseService,
    private supplierService: SupplierService,
    private helper: PurchaseHelperService,
    private tooltipService: TooltipService,
    private notification: NotificationService,
    private route: Router,
    protected permission: PurchasePermissionsService
  ){
    this.purchase = helper.createEmptyPurchase();
  }

  /** Inicializa tooltips al renderizar el componente */
  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  ngOnInit(): void {
    this.getSuppliers();
    this.getTotalPurchase();
  }

  /** 
   * Crear un nuevo proceso de compra
   * Redirige a la ruta de stock 
   * */
  createNewPurchaseProcess(): void{
    if (this.validator.validateForm(this.purchase)|| this.purchaseDetailList) {
      this.requestDTO = this.helper.createDto(this.purchase, this.purchaseDetailList);
      this.purchaseService.createPurchase(this.requestDTO).subscribe((response) => {
        console.log('Compra agregada exitosamente: ', response);
        this.notification.showSuccessToast(`¡Compra N°${this.TotalPurchaseDB} fue agregada exitosamente!`, 'top', 3000);
        this.route.navigate(['/stock']);
      }, (error) =>{
        const message = error.error?.message || error.error?.error;
        console.error('Error al agregar la compra: ', error);
        this.notification.showErrorToast(`Error al agregar la compra \n${message}`, 'top', 5000);
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /** Obtiene una lista de proveedores */
  getSuppliers(): void{
    this.supplierService.getSuppliers().subscribe((list) => {
      this.supplierList = list;
    })
  }

  /** Obtiene el numero total de compras realizadas */
  getTotalPurchase(): void{
    this.purchaseService.getTotalPurchase().subscribe((totalPurchases)=>{
      this.TotalPurchaseDB = totalPurchases;
    });
  }

  /**
   * Agrega un producto seleccionado a la lista de detalles de compra
   * valida si el producto ya fue agregado antes a la lista
   * calcula los valores de impuesto y precio total del detalle
   * @param product - Producto seleccionado para agregar a la lista
   */
  addSelectedProductToPurchaseDetailList(product: Product): void{
    this.productSelected = product;
    const isProductAdded = this.purchaseDetailList.some((detail) => detail.product.id === product.id);
    if (!isProductAdded) {
      const newPurchaseDetail: PurchaseDetails = {
        id: 0,
        quantity: 0,
        price: 0,
        tax: 0,
        total: 0,
        purchase: this.purchase,
        product: product
      }
  
      newPurchaseDetail.tax = newPurchaseDetail.price * 0.19;
      newPurchaseDetail.total = newPurchaseDetail.price + newPurchaseDetail.tax;
      this.purchaseDetailList.push(newPurchaseDetail);
      this.sumTotalAndTaxList();
    } else {
      this.notification.showErrorToast('Producto ya agregado!','center',1500);
    }
  }

  /**
   * Elimina un producto de la lista de detalle
   * @param index - Identificador del detalle en la lista
   */
  removeDetail(index: number): void {
    this.purchaseDetailList.splice(index, 1);
    this.sumTotalAndTaxList();
  }

  /**
   * Actualiza los totales de un detalle de compra
   * @param purchaseDetail - Detalle al cual debe actualizar
   */
  updateTotal(purchaseDetail: PurchaseDetails): void {
    purchaseDetail.tax = purchaseDetail.price * 0.19;
    if(purchaseDetail.quantity < 0 || purchaseDetail.price < 0){
      purchaseDetail.quantity = 0;
      purchaseDetail.price = 0;
      purchaseDetail.total = 0;
    }else{
      purchaseDetail.total = purchaseDetail.price * purchaseDetail.quantity + purchaseDetail.tax;
    }
    this.sumTotalAndTaxList();
  }

  /** Calcula el total y los impuestos acumulados de la lista de detalles */
  sumTotalAndTaxList(): void{
    this.total = 0;
    this.purchase.tax = 0;
    this.purchaseDetailList.forEach((purchaseDetail) =>{
      this.total += purchaseDetail.total;
      this.purchase.tax += purchaseDetail.tax;
    });
  }

  /** Reinicia los datos de la compra */
  resetData(): void{
    this.purchase = this.helper.createEmptyPurchase();
    this.purchaseDetailList = [];
    this.total = 0;
  }

  /** Habilita la edición de un campo en un detalle específico */
  enableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.id.toString(); // Usa el ID del detalle como clave
    if (!this.editingFields[key]) {
      this.editingFields[key] = {};
    }
    this.editingFields[key][field] = true;
  }
  
  /** Deshabilita la edición de un campo en un detalle específico */
  disableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.id.toString();
    if (this.editingFields[key]) {
      this.editingFields[key][field] = false;
    }
    this.updateTotal(detail); // Actualiza el total cuando se desactiva la edición
  }
  
  /** Verifica si un campo específico está en edición */
  isEditing(detail: PurchaseDetails, field: 'quantity' | 'price'): boolean {
    const key = detail.id.toString();
    return !!this.editingFields[key]?.[field];
  }
}
