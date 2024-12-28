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
    private helper: ProductHelperService
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
  
  resetForms(): void{
    this.newBrand = this.helper.createEmptyBrand();
    this.newCategory = this.helper.createEmptyCategory();
    this.newSubcategory = this.helper.createEmptySubcategory();
    this.newUnit = this.helper.createEmptyUnit();
    this.category = null;
  }
}
