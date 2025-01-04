import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../../services/Purchase/supplier.service';
import { Supplier } from '../../../../models/Entity/Purchase/supplier';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit{

  suppliers: Supplier[] = [];
  selectedSupplier: Supplier | null = null;
  status: boolean = false;

  constructor(
    private supplierService: SupplierService,
  ){}

  ngOnInit(): void {
    this.getSuppliers();
  }

  getSuppliers(): void{
    this.supplierService.getSuppliers().subscribe( (supplierList) =>{
      this.suppliers = supplierList;
    }, (error) =>{
      console.log('Error no se encontró ningún proveedor', error);
    });
  }

  getSelectedSupplier(supplier: Supplier): void{
    this.selectedSupplier = supplier;
  }

  openFormat(statusClick: boolean): void{
    this.status = statusClick;
    this.selectedSupplier = null;
  }
}
