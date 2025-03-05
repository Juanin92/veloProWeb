import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StatusProduct } from '../../../models/enum/status-product';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent implements OnInit{

  @Input() showStatusColumn: boolean = true;
  @Input() showStockColumn: boolean = false;
  @Input() filterList: boolean = false;
  @Output() productSelected = new EventEmitter<Product>();
  productList: Product[] = [];
  filteredProductsList: Product[] = [];
  textFilter: string = '';

  constructor(
    private productService: ProductService,
    private notification: NotificationService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void{
    this.getProducts();
  }
  
  /** Obtiene una lista de productos */
  getProducts(): void {
    this.productService.getProducts().subscribe((list) => {
      this.productList = list;
      if (this.filterList) {
        this.filteredProductsList = list.filter(
          product => product.statusProduct === StatusProduct.AVAILABLE && 
                      product.salePrice > 0);
      }else{
        this.filteredProductsList = list;
      }
    });
  }

  selectProduct(product: Product): void {
    if (product.statusProduct === StatusProduct.DISCONTINUED) {
      this.notification.showWarningToast('Debes activar el producto', 'top', 3000);
    }else{
      this.productSelected.emit(product);
    }
  }

  /**
  * Retorna el color asociado con el estado del producto.
  * @param status - El estado del producto.
  * @returns - El color correspondiente al estado del producto.
  */
  statusColor(status: string): string {
    switch (status) {
      case 'DISPONIBLE': return 'rgb(40, 238, 40)';
      case 'DESCONTINUADO': return 'red';
      case 'NODISPONIBLE': return 'rgb(9, 180, 237)';
      default: return 'transparent';
    }
  }

  /**
   * Filtrar lista de productos según el criterio de búsqueda
   * Se filtrara por nombre de marca, categoría, subcategoría y descripción donde textFilter
   * contendrá el valor a filtrar
   */
  searchFilterCustomer(): void {
    if (this.textFilter.trim() === '') {
      this.filteredProductsList = this.filteredProductsList;
    } else {
      this.filteredProductsList = this.filteredProductsList.filter(product =>
        product.brand.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.category.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.subcategoryProduct.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.description.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
