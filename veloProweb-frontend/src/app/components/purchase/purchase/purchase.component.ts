import { Component, OnInit } from '@angular/core';
import { Purchase } from '../../../models/Entity/Purchase/purchase';
import { PurchaseValidator } from '../../../validation/purchase-validator';
import { CommonModule } from '@angular/common';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.css'
})
export class PurchaseComponent implements OnInit{
  
  purchase: Purchase;
  supplierList: Supplier[] = [];
  productList: Product[] = [];
  validator = PurchaseValidator;

  constructor(
    private supplierService: SupplierService,
    private productService: ProductService
  ){
    this.purchase = this.createEmptyPurchase();
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
