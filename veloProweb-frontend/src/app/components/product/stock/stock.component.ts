import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { CommonModule, NgStyle } from '@angular/common';
import { ProductDTO } from '../../../models/DTO/product-dto';
import { FormsModule } from '@angular/forms';
import { AddProductComponent } from "../add-product/add-product.component";
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { UpdateProductComponent } from "../update-product/update-product.component";
import { StatusProduct } from '../../../models/enum/status-product';

@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, NgStyle, FormsModule, AddProductComponent, UpdateProductComponent],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit{

  products: ProductDTO[] = [];
  filteredProducts: ProductDTO[] = [];
  selectedProduct: Product;
  textFilter: string = '';

  constructor(
    private stockService: ProductService,
    private helper: ProductHelperService){
      this.selectedProduct = helper.createEmptyProduct();
      // this.selectedProduct = {
      //   id: 0,
      //   description: '',
      //   salePrice: 0,
      //   buyPrice: 0,
      //   stock: 0,
      //   status: false,
      //   statusProduct: StatusProduct.UNAVAILABLE,
      //   brand: '',
      //   unit: '',
      //   subcategoryProduct: '',
      //   category: ''
      // }
    }

  ngOnInit(): void {
    this.getProducts();
  }

  getProducts(): void{
    this.stockService.getProducts().subscribe((list) =>{
      this.products = list;
      this.filteredProducts = list;
      console.log('Producto: ', this.products);
    }, (error) => {
      console.log('Error no se encontró ningún producto', error);
    });
  }

  openModalCustomer(product: Product): void {
      if (product) {
        this.selectedProduct = { ...product };
      } else {
        console.error('No se pudo abrir modal, el producto no esta definido');
      }
    }

  statusColor(status: string): string {
    switch (status) {
      case 'DISPONIBLE': return 'rgb(40, 238, 40)';
      case 'DESCONTINUADO': return 'red';
      case 'NODISPONIBLE': return 'rgb(9, 180, 237)';
      default: return 'transparent';
    }
  }

  searchFilterCustomer(): void {
    if (this.textFilter.trim() === '') {
      this.filteredProducts = this.products;
    } else {
      this.filteredProducts = this.products.filter(product =>
        product.brand.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.category.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.subcategoryProduct.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        product.description.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
