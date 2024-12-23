import { Component, OnInit } from '@angular/core';
import { Product } from '../../../models/Entity/Product/product.model';
import { ProductValidator } from '../../../validation/product-validator';
import { ProductService } from '../../../services/Product/product.service';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrandService } from '../../../services/Product/brand.service';
import { Brand } from '../../../models/Entity/Product/brand';
import { UnitService } from '../../../services/Product/unit.service';
import { UnitProductModel } from '../../../models/Entity/Product/unit-product';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent implements OnInit{

  newProduct: Product;
  validator = ProductValidator;
  brandList: Brand[] = [];
  unitList: UnitProductModel[] = [];

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private unitService: UnitService,
    private helper: ProductHelperService,
    private notification: NotificationService){
    this.newProduct = helper.createEmptyProduct();
  }

  ngOnInit(): void {
    this.getAllBrands();
    this.getAllUnits();
  }

  getAllBrands(): void{
    this.brandService.getBrands().subscribe((list) => {
      this.brandList = list;
      console.log('marcas: ', this.brandList);
    }, (error) => {
      console.log('Error no se encontró ninguna marca', error);
    })
  }

  getAllUnits(): void{
    this.unitService.getUnits().subscribe((list) => {
      this.brandList = list;
      console.log('marcas: ', this.brandList);
    }, (error) => {
      console.log('Error no se encontró ninguna marca', error);
    })
  }

  addProduct(): void{
    if(this.validator.validateForm(this.newProduct)){
      
    }else{
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }
}
