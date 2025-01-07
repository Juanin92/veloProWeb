import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.css'
})
export class UpdateProductComponent implements OnChanges{

  @Input() selectedProduct: Product;
  @Output() productUpdated = new EventEmitter<void>();
  validator = ProductValidator;
  stockChanged: boolean = false;
  originalStock: number;

  constructor(
    private productService: ProductService,
    private helper: ProductHelperService,
    private notification: NotificationService) {
    this.selectedProduct = helper.createEmptyProduct();
    this.originalStock = this.selectedProduct.stock;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedProduct']) {
      this.originalStock = changes['selectedProduct'].currentValue.stock;
    }
  }

  stockChangeValidation(status: boolean): void{
    console.log('Stock original: ', this.selectedProduct.stock);
    console.log('Stock actual: ', this.originalStock);
    if (this.selectedProduct.stock !== this.originalStock) {
      this.stockChanged = true;
    }
    if (!this.stockChanged) {
      this.updateProduct(); 
    }
    if (!status) {
      this.stockChanged = false;
    }
  }

  updateProduct(): void{
    if (this.selectedProduct && this.validator.validateForm(this.selectedProduct)) {
      const updatedProduct = {...this.selectedProduct};
      this.productService.updateProduct(updatedProduct).subscribe((response) =>{
        console.log('Se actualizo el producto: ', updatedProduct);
        this.notification.showSuccessToast(`Se actualizo el producto ${updatedProduct.description} correctamente`, 'top', 3000);
        this.productUpdated.emit();
        this.selectedProduct = this.helper.createEmptyProduct();
        this.originalStock = this.selectedProduct.stock;
        this.stockChanged = false;
      }, (error) => {
        const message = error.error.error;
        this.notification.showErrorToast(`Error al actualizar producto \n${message}`, 'top', 5000);
        console.log('Error al actualizar producto: ', message);
      });
    }
  }
}
