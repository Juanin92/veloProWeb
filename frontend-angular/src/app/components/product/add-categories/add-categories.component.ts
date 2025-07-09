import { Component, OnInit } from '@angular/core';
import { BrandService } from '../../../services/product/brand.service';
import { CategoryService } from '../../../services/product/category.service';
import { SubcategoryService } from '../../../services/product/subcategory.service';
import { UnitService } from '../../../services/product/unit.service';
import { Brand } from '../../../models/entity/product/brand';
import { Category } from '../../../models/entity/product/category';
import { Subcategory } from '../../../models/entity/product/subcategory';
import { UnitProduct } from '../../../models/entity/product/unit-product';
import { ProductHelperService } from '../../../services/product/product-helper.service';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../utils/notification-service.service';
import { ProductPermissionsService } from '../../../services/permissions/product-permissions.service';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-add-categories',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-categories.component.html',
  styleUrl: './add-categories.component.css',
})
export class AddCategoriesComponent implements OnInit {
  newBrand: Brand;
  newCategory: Category;
  category: Category | null = null;
  newSubcategory: Subcategory;
  newUnit: UnitProduct;
  validator = ProductValidator;
  categoryList: Category[] = [];
  touchedFields: Record<string, boolean> = {};

  constructor(
    private brandService: BrandService,
    private categoryService: CategoryService,
    private subcategoryService: SubcategoryService,
    private unitService: UnitService,
    private helper: ProductHelperService,
    private notification: NotificationService,
    protected permission: ProductPermissionsService,
    private errorMessage: ErrorMessageService
  ) {
    this.newBrand = helper.createEmptyBrand();
    this.newCategory = helper.createEmptyCategory();
    this.newSubcategory = helper.createEmptySubcategory();
    this.newUnit = helper.createEmptyUnit();
  }

  ngOnInit(): void {
    this.getAllCategories();
  }

  /**
   * Obtiene una lista de todas las categorías.
   * asigna una lista con categorías a la lista categoryList
   */
  getAllCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (list) => this.categoryList = list,
    });
  }

  /**
   * Agregar un nueva marca.
   * Valida el formulario y si es correcto, llama al servicio para agregar la marca.
   * Muestra notificaciones dependiendo el estado de la acción 
   * y reset los valores del objeto marca.
   */
  createBrand(): void {
    if (this.validator.validateBrand(this.newBrand)) {
      this.brandService.createBrand(this.newBrand).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(
            `${this.newBrand.name} ha sido registrada`,
            'top',
            3000
          );
          this.newBrand = this.helper.createEmptyBrand();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`${message}`, 'top', 5000);
        }
      });
    }
  }

  /**
   * Agregar un nueva categoría.
   * Valida el formulario y si es correcto, llama al servicio para agregar la categoría.
   * Muestra notificaciones dependiendo el estado de la acción 
   * y reset los valores del objeto categoría.
   */
  createCategory(): void {
    if (this.validator.validateCategory(this.newCategory)) {
      this.categoryService.createCategory(this.newCategory).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(
            `${this.newCategory.name} ha sido registrada`,
            'top',
            3000
          );
          this.newCategory = this.helper.createEmptyCategory();
          this.getAllCategories();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`${message}`, 'top', 5000);
        }
      });
    }
  }

  /**
   * Agregar un nueva subcategoría.
   * Valida si categoría no esta nula y asigna el valor a la variable category
   * Valida el formulario y si es correcto, llama al servicio para agregar la subcategoría.
   * Muestra notificaciones dependiendo el estado de la acción 
   * y reset los valores del objeto subcategoría.
   */
  createSubcategory(): void {
    this.category && (this.newSubcategory.category = this.category);
    if (this.validator.validateSubcategory(this.newSubcategory)) {
      this.subcategoryService.createSubcategory(this.newSubcategory).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(
            `${this.newSubcategory.name} ha sido registrada`,
            'top',
            3000
          );
          this.newSubcategory = this.helper.createEmptySubcategory();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`${message}`, 'top', 5000);
        }
      });
    }
  }

  /**
   * Agregar un nueva unidad de medida.
   * Valida el formulario y si es correcto, llama al servicio para agregar la unidad de medida.
   * Muestra notificaciones dependiendo el estado de la acción 
   * y reset los valores del objeto unidad de medida.
   */
  createUnit(): void {
    if (this.validator.validateUnit(this.newUnit)) {
      this.unitService.createUnit(this.newUnit).subscribe({
        next:(response) => {
          this.notification.showSuccessToast(
            `${this.newUnit.nameUnit} ha sido registrada`,
            'top',
            3000
          );
          this.newUnit = this.helper.createEmptyUnit();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`${message}`, 'top', 5000);
        }
      });
    }
  }

  /**
   * Reset todos los valores sus atributos de los objetos
   */
  resetForms(): void {
    this.newBrand = this.helper.createEmptyBrand();
    this.newCategory = this.helper.createEmptyCategory();
    this.newSubcategory = this.helper.createEmptySubcategory();
    this.newUnit = this.helper.createEmptyUnit();
    this.category = null;
    this.touchedFields = {};
  }
}
