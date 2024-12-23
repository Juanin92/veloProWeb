import { Component } from '@angular/core';
import { Product } from '../../../models/Entity/Product/product.model';
import { ProductValidator } from '../../../validation/product-validator';
import { ProductService } from '../../../services/Product/product.service';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent {

  newProduct: Product;
  validator = ProductValidator;

  constructor(
    private productService: ProductService,
    private helper: ProductHelperService,
    private notification: NotificationService){
    this.newProduct = helper.createEmptyProduct();
  }

  addProduct(): void{
    if(this.validator.validateForm(this.newProduct)){
      
    }else{
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

}
