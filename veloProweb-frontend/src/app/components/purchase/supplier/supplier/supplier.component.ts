import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../../services/Purchase/supplier.service';
import { Supplier } from '../../../../models/Entity/Purchase/supplier';
import { CommonModule } from '@angular/common';
import { SupplierValidator } from '../../../../validation/supplier-validator';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../../../utils/notification-service.service';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit{

  suppliers: Supplier[] = [];
  newSupplier: Supplier;
  selectedSupplier: Supplier | null = null;
  status: boolean = false;
  validator = SupplierValidator;

  constructor(
    private supplierService: SupplierService,
    private notification: NotificationService){
      this.newSupplier = this.initializeSupplier();
    }

  ngOnInit(): void {
    this.getSuppliers();
  }

  createSupplier(): void{
    if (this.validator.validateForm(this.newSupplier)) {
      this.supplierService.createSupplier(this.newSupplier).subscribe((response) =>{
        console.log('Proveedor agregado exitosamente:', response);
        this.notification.showSuccessToast(`¡${this.newSupplier.name} fue agregado exitosamente!`, 'top', 3000);
          this.initializeSupplier(); // Reinicia la variable del cliente vacío.
          setTimeout(() => {
            window.location.reload();
          }, 3000);
      }, (error) =>{
        const message = error.error.error;
        console.error('Error al agregar el proveedor:', error);
        this.notification.showErrorToast(`Error al agregar proveedor \n${message}`, 'top', 5000);
      })
    }else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
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
    this.status = false;
  }

  openFormat(statusClick: boolean): void{
    this.status = statusClick;
    this.selectedSupplier = null;
    this.newSupplier = this.initializeSupplier();
  }

  initializeSupplier(): Supplier{
    return this.newSupplier = {
      id: 0,
      name: '',
      email: '',
      rut: '',
      phone: '+569 '
    }
  }
}
