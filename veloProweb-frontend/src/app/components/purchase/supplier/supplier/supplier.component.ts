import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../../services/Purchase/supplier.service';
import { Supplier } from '../../../../models/Entity/Purchase/supplier';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit{

  suppliers: Supplier[] = [];

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
}
