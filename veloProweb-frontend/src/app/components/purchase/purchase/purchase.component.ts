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
  selectedProductsList: Product[] = [];
  validator = PurchaseValidator;

  constructor(
    private supplierService: SupplierService,
    private productService: ProductService,
    private tooltipService: TooltipService
  ){
    this.purchase = this.createEmptyPurchase();
  }

  ngAfterViewInit(): void {
    this.tooltipService.initializeTooltips();
  }

  ngOnInit(): void {
    this.getSuppliers();
    this.getProducts();
  }

  getSuppliers(): void{
    this.supplierService.getSuppliers().subscribe((list) => {
      this.supplierList = list;
    })
  }

  getProducts(): void{
    this.productService.getProducts().subscribe((list) => {
      this.productList = list;
    });
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
}
