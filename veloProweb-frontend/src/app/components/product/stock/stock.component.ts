import { Component, OnInit } from '@angular/core';
import { StockService } from '../../../services/Product/stock.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { CommonModule, NgStyle } from '@angular/common';
import { ProductDTO } from '../../../models/DTO/product-dto';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, NgStyle, FormsModule],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit{

  products: ProductDTO[] = [];
  filteredProducts: ProductDTO[] = [];
  textFilter: string = '';

  constructor(private stockService: StockService){}

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
