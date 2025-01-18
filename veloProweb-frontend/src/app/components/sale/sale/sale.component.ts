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

@Component({
  selector: 'app-sale',
  standalone: true,
  imports: [FormsModule, CommonModule, ProductListComponent],
  templateUrl: './sale.component.html',
  styleUrl: './sale.component.css'
})
export class SaleComponent implements AfterViewInit, OnInit{

  sale: Sale;
  customerList: Customer[] = [];
  productSelected: Product | null = null;
  saleDetailList: SaleDetail[] = [];
  editingFields: { [key: string]: { quantity?: boolean; } } = {}; // (Map) Campos de edición activa para cantidades o precios en detalles de compra


  constructor(
    private customerService: CustomerService,
    private tooltipService: TooltipService,
    private notification: NotificationService,
    private helper: SaleHelperService){ 
      this.sale = helper.createEmptySale();
    }

  ngOnInit(): void {
    this.getCustomer();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  /**
     * Agrega un producto seleccionado a la lista de detalles de compra
     * valida si el producto ya fue agregado antes a la lista
     * calcula los valores de impuesto y precio total del detalle
     * @param product - Producto seleccionado para agregar a la lista
     */
    addSelectedProductToPurchaseDetailList(product: Product): void{
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
    
        newSaleDetail.tax = newSaleDetail.price * 0.19;
        newSaleDetail.total = newSaleDetail.price * newSaleDetail.quantity;
        this.saleDetailList.push(newSaleDetail);
        // this.sumTotalAndTaxList();
      } else {
        this.notification.showErrorToast('Producto ya agregado!','center',1500);
      }
    }

    getCustomer(): void{
      this.customerService.getCustomer().subscribe((list)=>{
        this.customerList = list;
      });
    }

  /**
   * Elimina un producto de la lista de detalle
   * @param index - Identificador del detalle en la lista
   */
  removeDetail(index: number): void {
    this.saleDetailList.splice(index, 1);
    // this.sumTotalAndTaxList();
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
      // this.updateTotal(detail); // Actualiza el total cuando se desactiva la edición
    }
    
    /** Verifica si un campo específico está en edición */
    isEditing(detail: SaleDetail): boolean {
      const key = detail.id.toString();
      return !!this.editingFields[key]?.['quantity'];
    }
}
