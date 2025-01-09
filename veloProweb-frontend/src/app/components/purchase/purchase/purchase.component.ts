import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Purchase } from '../../../models/Entity/Purchase/purchase';
import { PurchaseValidator } from '../../../validation/purchase-validator';
import { CommonModule } from '@angular/common';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { TooltipService } from '../../../utils/tooltip.service';
import { PurchaseDetails } from '../../../models/Entity/Purchase/purchase-details';
import { NotificationService } from '../../../utils/notification-service.service';
import { PurchaseService } from '../../../services/Purchase/purchase.service';

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.css'
})
export class PurchaseComponent implements OnInit, AfterViewInit{
  
  purchase: Purchase;
  supplierList: Supplier[] = [];
  productList: Product[] = [];
  filteredProductsList: Product[] = [];
  purchaseDetailList: PurchaseDetails[] = [];
  validator = PurchaseValidator;
  total: number = 0;
  textFilter: string = '';
  TotalPurchaseDB: number = 0;
  editingFields: { [key: string]: { quantity?: boolean; price?: boolean } } = {};

  constructor(
    private purchaseService: PurchaseService,
    private supplierService: SupplierService,
    private productService: ProductService,
    private tooltipService: TooltipService,
    private notification: NotificationService
  ){
    this.purchase = this.createEmptyPurchase();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  ngOnInit(): void {
    this.getSuppliers();
    this.getProducts();
    this.getTotalPurchase();
  }

  getSuppliers(): void{
    this.supplierService.getSuppliers().subscribe((list) => {
      this.supplierList = list;
    })
  }

  getProducts(): void{
    this.productService.getProducts().subscribe((list) => {
      this.productList = list;
      this.filteredProductsList = list;
    });
  }

  getTotalPurchase(): void{
    this.purchaseService.getTotalPurchase().subscribe((totalPurchases)=>{
      this.TotalPurchaseDB = totalPurchases;
    });
  }

  addSelectedProductToPurchaseDetailList(product: Product): void{
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
    } else {
      this.notification.showErrorToast('Producto ya agregado!','center',1500);
    }
  }

  removeDetail(index: number): void {
    this.purchaseDetailList.splice(index, 1);
    this.sumTotalList();
  }

  updateTotal(purchaseDetail: PurchaseDetails): void {
    purchaseDetail.tax = purchaseDetail.price * 0.19;
    if(purchaseDetail.quantity < 0 || purchaseDetail.price < 0){
      purchaseDetail.quantity = 0;
      purchaseDetail.price = 0;
      purchaseDetail.total = 0;
    }else{
      purchaseDetail.total = purchaseDetail.price * purchaseDetail.quantity + purchaseDetail.tax;
    }
    this.sumTotalList();
  }

  sumTotalList(): void{
    this.total = 0;
    this.purchaseDetailList.forEach((purchaseDetail) =>{
      this.total += purchaseDetail.total;
    })
  }

  enableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.id.toString(); // Usa el ID del detalle como clave
    if (!this.editingFields[key]) {
      this.editingFields[key] = {};
    }
    this.editingFields[key][field] = true;
  }
  
  disableEdit(detail: PurchaseDetails, field: 'quantity' | 'price'): void {
    const key = detail.id.toString();
    if (this.editingFields[key]) {
      this.editingFields[key][field] = false;
    }
    this.updateTotal(detail); // Actualiza el total cuando se desactiva la edición
  }
  
  isEditing(detail: PurchaseDetails, field: 'quantity' | 'price'): boolean {
    const key = detail.id.toString();
    return !!this.editingFields[key]?.[field];
  }

  /**
   * Retorna el color asociado con el estado del producto.
   * @param status - El estado del producto.
   * @returns - El color correspondiente al estado del producto.
   */
  statusColor(status: string): string {
    switch (status) {
      case 'DISPONIBLE': return 'rgb(40, 238, 40)';
      case 'DESCONTINUADO': return 'red';
      case 'NODISPONIBLE': return 'rgb(9, 180, 237)';
      default: return 'transparent';
    }
  }

  createEmptyPurchase(): Purchase{
    return this.purchase ={ 
      id: 0,
      document: '',
      documentType: '',
      tax: 0,
      purchaseTotal: 0,
      date: '',
      supplier: null
    }
  }

  /**
   * Filtrar lista de productos según el criterio de búsqueda
   * Se filtrara por nombre de marca, categoría, subcategoría y descripción donde textFilter
   * contendrá el valor a filtrar
   */
  searchFilterCustomer(): void {
    if (this.textFilter.trim() === '') {
      this.filteredProductsList = this.productList;
    } else {
      this.filteredProductsList = this.productList.filter(product =>
        product.brand.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.category.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.subcategoryProduct.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.description.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
