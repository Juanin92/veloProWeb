import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { CommonModule, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AddProductComponent } from "../add-product/add-product.component";
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { UpdateProductComponent } from "../update-product/update-product.component";
import { AddCategoriesComponent } from "../add-categories/add-categories.component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, NgStyle, FormsModule, AddProductComponent, UpdateProductComponent, AddCategoriesComponent],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit{

  products: Product[] = [];
  filteredProducts: Product[] = [];
  selectedProduct: Product;
  textFilter: string = '';

  constructor(
    private stockService: ProductService,
    private helper: ProductHelperService,
    private router: Router){
      this.selectedProduct = helper.createEmptyProduct();
    }

  /**
   * Inicializa el componente cargando la lista de productos
   */
  ngOnInit(): void {
    this.getProducts();
  }

  /**
   * Obtiene una lista de todos los productos.
   * asigna una lista con productos a la lista products y filteredProducts
   */
  getProducts(): void{
    this.stockService.getProducts().subscribe((list) =>{
      this.products = list;
      this.filteredProducts = list;
    }, (error) => {
      console.log('Error no se encontró ningún producto', error);
    });
  }

  /**
   * Abrir modal con una copia de un producto seleccionado
   * @param product - producto seleccionado
   */
  openModalCustomer(product: Product): void {
      if (product) {
        this.selectedProduct = { ...product };
      } else {
        console.error('No se pudo abrir modal, el producto no esta definido');
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

  navigateToSupplier(): void{
    this.router.navigate(['/proveedores']);
  }

  /**
   * Filtrar lista de productos según el criterio de búsqueda
   * Se filtrara por nombre de marca, categoría, subcategoría y descripción donde textFilter
   * contendrá el valor a filtrar
   */
  searchFilterCustomer(): void {
    if (this.textFilter.trim() === '') {
      this.filteredProducts = this.products;
    } else {
      this.filteredProducts = this.products.filter(product =>
        product.brand.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.category.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.subcategoryProduct.name.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.description.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
