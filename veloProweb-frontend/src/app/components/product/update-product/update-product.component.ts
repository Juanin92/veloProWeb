import { Component, Input } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProductDTO } from '../../../models/DTO/product-dto';
import { StatusProduct } from '../../../models/enum/status-product';

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.css'
})
export class UpdateProductComponent {

  @Input() selectedProduct: Product;
  validator = ProductValidator;

  constructor(
    private productService: ProductService,
    private helper: ProductHelperService,
    private notification: NotificationService){
      this.selectedProduct = helper.createEmptyProduct();
      // this.selectedProduct = {
      //         id: 0,
      //         description: '',
      //         salePrice: 0,
      //         buyPrice: 0,
      //         stock: 0,
      //         status: false,
      //         statusProduct: StatusProduct.UNAVAILABLE,
      //         brand: '',
      //         unit: '',
      //         subcategoryProduct: '',
      //         category: ''
      //       }
    }

}
