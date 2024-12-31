import { Component, OnInit} from '@angular/core';
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
import { CategoryService } from '../../../services/Product/category.service';
import { SubcategoryService } from '../../../services/Product/subcategory.service';
import { Category } from '../../../models/Entity/Product/category';
import { Subcategory } from '../../../models/Entity/Product/subcategory';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent implements OnInit {

  newProduct: Product;
  brandSelected: Brand | null = null;
  unitSelected: UnitProductModel | null = null;
  categorySelected: Category | null = null;
  subcategorySelected: Subcategory | null = null;
  validator = ProductValidator;
  brandList: Brand[] = [];
  unitList: UnitProductModel[] = [];
  categoryList: Category[] = [];
  subcategoryList: Subcategory[] = [];

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private unitService: UnitService,
    private categoryService: CategoryService,
    private subcategoryService: SubcategoryService,
    private helper: ProductHelperService,
    private notification: NotificationService) {
    this.newProduct = helper.createEmptyProduct();
  }

  ngOnInit(): void {
    this.getAllBrands();
    this.getAllUnits();
    this.getAllCategories();
  }

  getAllBrands(): void {
    this.brandService.getBrands().subscribe((list) => {
      this.brandList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna marca', error);
    })
  }

  getAllUnits(): void {
    this.unitService.getUnits().subscribe((list) => {
      this.unitList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna unidad de medida', error);
    })
  }

  getAllCategories(): void {
    this.categoryService.getCategories().subscribe((list) => {
      this.categoryList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna categoría', error);
    })
  }

  getAllSubcategories(categoryID: number): void {
    this.subcategoryService.getSubCategoriesByCategory(categoryID).subscribe((list) => {
      this.subcategoryList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna subcategoría', error);
    })
  }

  onCategoryChange(event: Event): void {
    if (this.categorySelected) {
      this.getAllSubcategories(this.categorySelected.id);
    }
  }

  addProduct(): void {
    if (this.validator.validateForm(this.newProduct)) {

    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  updateNewProductFields(): void {
    if (this.brandSelected) {
      this.newProduct.brand = this.brandSelected;
    }
    if (this.categorySelected) {
      this.newProduct.category = this.categorySelected;
    }
    if (this.subcategorySelected) {
      this.newProduct.subcategoryProduct = this.subcategorySelected;
    }
    if (this.unitSelected) {
      this.newProduct.unit = this.unitSelected;
    }
  }

  addToDescription(value: string | undefined): void {
    if (value) {
      this.newProduct.description += value + ' ';
    }
  }

  resetProductForm(): void {
    this.newProduct = this.helper.createEmptyProduct();
    this.brandSelected = null;
    this.categorySelected = null;
    this.subcategorySelected = null;
    this.unitSelected = null;
    this.subcategoryList = [];
  }
}
