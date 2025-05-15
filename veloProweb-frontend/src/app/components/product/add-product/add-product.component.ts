import { Component, EventEmitter, OnInit, output, Output} from '@angular/core';
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
import { UnitProduct } from '../../../models/Entity/Product/unit-product';
import { CategoryService } from '../../../services/Product/category.service';
import { SubcategoryService } from '../../../services/Product/subcategory.service';
import { Category } from '../../../models/Entity/Product/category';
import { Subcategory } from '../../../models/Entity/Product/subcategory';
import { ModalService } from '../../../utils/modal.service';
import { ProductPermissionsService } from '../../../services/Permissions/product-permissions.service';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent implements OnInit {

  @Output() productAdded = new EventEmitter<void>();
  newProduct: Product;
  brandSelected: Brand | null = null;
  unitSelected: UnitProduct | null = null;
  categorySelected: Category | null = null;
  subcategorySelected: Subcategory | null = null;
  validator = ProductValidator;
  brandList: Brand[] = [];
  unitList: UnitProduct[] = [];
  categoryList: Category[] = [];
  subcategoryList: Subcategory[] = [];
  touchedFields: Record<string, boolean> = {};

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private unitService: UnitService,
    private categoryService: CategoryService,
    private subcategoryService: SubcategoryService,
    private helper: ProductHelperService,
    private notification: NotificationService,
    public modalService: ModalService,
    protected permission: ProductPermissionsService) {
    this.newProduct = helper.createEmptyProduct();
  }

  /**
   * Inicializa el componente cargando las lista de categorías, marcas y unidades de medidas
   */
  ngOnInit(): void {
    this.loadData();
  }

  /**
   * Encargado de cargar los métodos
   */
  loadData(): void{
    this.getAllBrands();
    this.getAllUnits();
    this.getAllCategories();
    this.touchedFields = {};
  }

  /**
   * Obtiene una lista de todas las marcas.
   * asigna una lista con marcas a la lista brandList
   */
  getAllBrands(): void {
    this.brandService.getBrands().subscribe((list) => {
      this.brandList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna marca', error);
    })
  }

  /**
   * Obtiene una lista de todas las unidades.
   * asigna una lista con unidades a la lista unitList
   */
  getAllUnits(): void {
    this.unitService.getUnits().subscribe((list) => {
      this.unitList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna unidad de medida', error);
    })
  }

  /**
   * Obtiene una lista de todas las categorías.
   * asigna una lista con categorías a la lista categoryList
   */
  getAllCategories(): void {
    this.categoryService.getCategories().subscribe((list) => {
      this.categoryList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna categoría', error);
    })
  }

  /**
   * Obtiene una lista de todas las subcategorías.
   * asigna una lista con subcategorías a la lista subcategoryList
   */
  getAllSubcategories(categoryID: number): void {
    this.subcategoryService.getSubCategoriesByCategory(categoryID).subscribe((list) => {
      this.subcategoryList = list;
    }, (error) => {
      console.log('Error no se encontró ninguna subcategoría', error);
    })
  }

  /**
   * Maneja el cambio de categoría para cargar las subcategorías correspondientes.
   * Este método es llamado cuando el usuario selecciona una categoría diferente.
   * Si hay una categoría seleccionada, obtiene y carga las subcategorías relacionadas a esa categoría.
   * @param event - evento de cambio disparado cuando el usuario selecciona una categoría en el formulario.
   */
  onCategoryChange(event: Event): void {
    if (this.categorySelected) {
      this.getAllSubcategories(this.categorySelected.id);
    }
  }

  /**
   * Agregar un nuevo producto.
   * Valida el formulario y si es correcto, llama al servicio para agregar la producto.
   * Muestra notificaciones dependiendo el estado de la acción, reset los valores del objeto producto
   * y refresca la pagina.
   */
  addProduct(): void {
    if (this.validator.validateForm(this.newProduct)) {
      this.productService.createProduct(this.newProduct).subscribe((response) => {
        console.log("Producto creado exitosamente! ", this.newProduct);
        this.notification.showSuccessToast("Producto creado exitosamente!", 'top', 3000);
        this.resetProductForm();
        this.productAdded.emit();
        this.modalService.closeModal();
      }, (error) => {
        const message = error.error?.message || error.error?.error;
          console.error('Error:', error);
          this.notification.showErrorToast(`Error: ${message}`, 'top', 5000);
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Actualiza los campos del nuevo producto con las selecciones actuales.
   */
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

  /**
   * Agrega un valor al atributo descripción del producto.
   * @param value - El valor a agregar a la descripción.
   */
  addToDescription(value: string | undefined): void {
    if (value) {
      this.newProduct.description += value + ' ';
    }
  }

  /**
   * Reset todos los valores sus atributos de los objetos 
   */
  resetProductForm(): void {
    this.newProduct = this.helper.createEmptyProduct();
    this.brandSelected = null;
    this.categorySelected = null;
    this.subcategorySelected = null;
    this.unitSelected = null;
    this.subcategoryList = [];
    this.touchedFields = {};
  }
}
