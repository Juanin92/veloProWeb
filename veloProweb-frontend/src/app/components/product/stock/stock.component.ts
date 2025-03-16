import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { CommonModule, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AddProductComponent } from "../add-product/add-product.component";
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { UpdateProductComponent } from "../update-product/update-product.component";
import { AddCategoriesComponent } from "../add-categories/add-categories.component";
import { RouterModule } from '@angular/router';
import { StatusProduct } from '../../../models/enum/status-product';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { TooltipService } from '../../../utils/tooltip.service';

@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, NgStyle, FormsModule, RouterModule, AddProductComponent, UpdateProductComponent, AddCategoriesComponent],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit, AfterViewInit {

  products: Product[] = [];
  filteredProducts: Product[] = [];
  selectedProduct: Product;
  textFilter: string = '';
  statusProduct = StatusProduct;
  sortPosition: boolean = true;
  sortPurchase: boolean = true;
  sortSale: boolean = true;
  sortStock: boolean = true;
  sortReserved: boolean = true;

  constructor(
    private stockService: ProductService,
    private helper: ProductHelperService,
    private notification: NotificationService,
    public modalService: ModalService,
    private tooltipService: TooltipService,
    private renderer: Renderer2) {
    this.selectedProduct = helper.createEmptyProduct();
  }


  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltipService.initializeTooltips();
    });
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
  getProducts(): void {
    this.stockService.getProducts().subscribe((list) => {
      this.products = list;
      this.filteredProducts = list;
    }, (error) => {
      console.log('Error no se encontró ningún producto', error);
    });
  }

  /**
   * Elimina (Desactivar) un producto seleccionado 
   * Valida si el producto no es valor nulo, si es correcto
   * lanza una confirmación antes de eliminar al cliente
   * @param product - Producto seleccionado
   */
  deleteProduct(product: Product): void {
    this.selectedProduct = product;
    if (this.selectedProduct) {
      this.notification.showConfirmation(
        "¿Estas seguro?",
        "No podrás revertir la acción!",
        "Si eliminar!",
        "Cancelar"
      ).then((result) => {
        if (result.isConfirmed) {
          this.stockService.deleteProduct(this.selectedProduct).subscribe((response) => {
            console.log('Producto eliminado exitosamente:', response);
            this.notification.showSuccessToast(`Se Elimino el producto ${this.selectedProduct.description} correctamente`, 'top', 3000);
            this.getProducts();
          }, (error) => {
            const message = error.error.error;
            console.log('Error al eliminar producto: ', message);
            this.notification.showErrorToast(`Error al eliminar producto \n${message}`, 'top', 5000);
          });
        }
      })
    }
  }

  /**
   * Activa el estado de un producto seleccionado
   * Valida si producto no es nulo y si es correcto, llama al servicio para activar al producto
   * @param product - Producto seleccionado
   */
  activateProduct(product: Product): void {
    this.selectedProduct = product;
    if (this.selectedProduct) {
      this.stockService.activeProduct(this.selectedProduct).subscribe((response) => {
        console.log("Producto Activado");
        this.notification.showSuccessToast(`Se activo nuevamente ${this.selectedProduct.description}.`, 'top', 3000);
        this.getProducts();
      }, (error) => {
        const message = error.error.error;
        console.log('Error al activar producto: ', message);
        this.notification.showErrorToast(`Error al activar el producto \n${message}`, 'top', 5000);
      });
    }
  }

  /**
   * Abrir modal con una copia de un producto seleccionado
   * @param product - producto seleccionado
   */
  openModalProduct(product: Product): void {
    if (product) {
      this.selectedProduct = { ...product };
      this.modalService.openModal();
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

  toggleSortPosition(): void{
    this.filteredProducts.reverse();
    this.sortPosition = !this.sortPosition;
  }

  toggleSortSale(): void{
    this.sortSale = !this.sortSale;
    this.filteredProducts.sort((a, b) => {
        const dateA = a.salePrice;
        const dateB = b.salePrice;
        return this.sortSale ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortPurchase(): void{
    this.sortPurchase = !this.sortPurchase;
    this.filteredProducts.sort((a, b) => {
        const dateA = a.buyPrice;
        const dateB = b.buyPrice;
        return this.sortPurchase ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortStock(): void{
    this.sortStock = !this.sortStock;
    this.filteredProducts.sort((a, b) => {
        const dateA = a.stock;
        const dateB = b.stock;
        return this.sortStock ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortReserved(): void{
    this.sortReserved = !this.sortReserved;
    this.filteredProducts.sort((a, b) => {
        const dateA = a.reserve;
        const dateB = b.reserve;
        return this.sortReserved ? dateA - dateB : dateB - dateA;
    });
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
