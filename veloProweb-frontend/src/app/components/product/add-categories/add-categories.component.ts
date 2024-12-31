import { Component, OnInit } from '@angular/core';
import { BrandService } from '../../../services/Product/brand.service';
import { CategoryService } from '../../../services/Product/category.service';
import { SubcategoryService } from '../../../services/Product/subcategory.service';
import { UnitService } from '../../../services/Product/unit.service';
import { Brand } from '../../../models/Entity/Product/brand';
import { Category } from '../../../models/Entity/Product/category';
import { Subcategory } from '../../../models/Entity/Product/subcategory';
import { UnitProductModel } from '../../../models/Entity/Product/unit-product';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-add-categories',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-categories.component.html',
  styleUrl: './add-categories.component.css'
})
export class AddCategoriesComponent implements OnInit{

  newBrand: Brand;
  newCategory: Category;
  category: Category | null = null;
  newSubcategory: Subcategory;
  newUnit: UnitProductModel;
  validator = ProductValidator;
  categoryList: Category[] = [];

  constructor(
    private brandService: BrandService,
    private categoryService: CategoryService,
    private subcategoryService: SubcategoryService,
    private unitService: UnitService,
    private helper: ProductHelperService,
    private notification: NotificationService
  ){
    this.newBrand = helper.createEmptyBrand();
    this.newCategory = helper.createEmptyCategory();
    this.newSubcategory = helper.createEmptySubcategory();
    this.newUnit = helper.createEmptyUnit();
  }
  ngOnInit(): void {
    this.getAllCategories();
  }

  getAllCategories(): void {
    this.categoryService.getCategories().subscribe((list) => {
      this.categoryList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna categoría', error);
    })
  }

  createBrand(): void{
    if(this.validator.validateBrand(this.newBrand)){
      this.brandService.createBrand(this.newBrand).subscribe((response) => {
        console.log('Nueva marca registrada', response);
        this.notification.showSuccessToast(`${this.newBrand.name} ha sido registrada`,'top', 3000);
        this.newBrand = this.helper.createEmptyBrand();
      }, (error) => {
        const message = error.error.message;
        console.error('Error:', error);
        this.notification.showErrorToast(`${message}`, 'top', 5000);
      });
    }
  }

  createCategory(): void{
    if(this.validator.validateCategory(this.newCategory)){
      this.categoryService.createCategory(this.newCategory).subscribe((response) => {
        console.log('Nueva categoría registrada', response);
        this.notification.showSuccessToast(`${this.newCategory.name} ha sido registrada`,'top', 3000);
        this.newCategory = this.helper.createEmptyCategory();
      }, (error) => {
        const message = error.error.message;
        console.error('Error:', error);
        this.notification.showErrorToast(`${message}`, 'top', 5000);
      });
    }
  }

  createSubcategory(): void{
    if(this.validator.validateSubcategory(this.newSubcategory)){
      this.subcategoryService.createSubcategory(this.newSubcategory).subscribe((response) => {
        console.log('Nueva subcategoría registrada', response);
        this.notification.showSuccessToast(`${this.newSubcategory.name} ha sido registrada`,'top', 3000);
        this.newSubcategory = this.helper.createEmptyCategory();
      }, (error) => {
        const message = error.error.message;
        console.error('Error:', error);
        this.notification.showErrorToast(`${message}`, 'top', 5000);
      });
    }
  }

  createUnit(): void{
    if(this.validator.validateUnit(this.newUnit)){
      this.unitService.createUnit(this.newUnit).subscribe((response) => {
        console.log('Nueva categoría registrada', response);
        this.notification.showSuccessToast(`${this.newUnit.nameUnit} ha sido registrada`,'top', 3000);
        this.newUnit = this.helper.createEmptyUnit();
      }, (error) => {
        const message = error.error.message;
        console.error('Error:', error);
        this.notification.showErrorToast(`${message}`, 'top', 5000);
      });
    }
  }
  
  resetForms(): void{
    this.newBrand = this.helper.createEmptyBrand();
    this.newCategory = this.helper.createEmptyCategory();
    this.newSubcategory = this.helper.createEmptySubcategory();
    this.newUnit = this.helper.createEmptyUnit();
    this.category = null;
  }
}
