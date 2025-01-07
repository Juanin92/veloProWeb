import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { SupplierValidator } from '../../../validation/supplier-validator';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit {

  suppliers: Supplier[] = [];
  newSupplier: Supplier;
  selectedSupplier: Supplier | null = null;
  status: boolean = false; //Estado para mostrar el formulario de creación
  updateStatus: boolean = false; //Estado para mostrar el formulario de actualización
  validator = SupplierValidator;

  constructor(
    private supplierService: SupplierService,
    private notification: NotificationService) {
    this.newSupplier = this.initializeSupplier();
  }

  ngOnInit(): void {
    this.getSuppliers();
  }

  /**
   * Agregar un nuevo proveedor.
   * Valida el formulario y si es correcto, llama al servicio para agregar el proveedor.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la página después de 3 seg.
   */
  createSupplier(): void {
    if (this.validator.validateForm(this.newSupplier)) {
      this.supplierService.createSupplier(this.newSupplier).subscribe((response) => {
        console.log('Proveedor agregado exitosamente:', response);
        this.notification.showSuccessToast(`¡${this.newSupplier.name} fue agregado exitosamente!`, 'top', 3000);
        this.initializeSupplier(); // Reinicia la variable del cliente vacío.
        setTimeout(() => {
          window.location.reload();
        }, 3000);
      }, (error) => {
        const message = error.error.error;
        console.error('Error al agregar el proveedor:', error);
        this.notification.showErrorToast(`Error al agregar proveedor \n${message}`, 'top', 5000);
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Actualizar datos de un proveedor.
   * Valida el formulario y si es correcto, llama al servicio para actualizar el proveedor.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la página después de 3 seg.
   */
  updateSupplier(): void {
    if (this.validator.validateForm(this.newSupplier)) {
      this.supplierService.updateSupplier(this.newSupplier).subscribe((response) => {
        console.log('Datos actualizados exitosamente:', response);
        this.notification.showSuccessToast(`¡${this.newSupplier.name} fue actualizado exitosamente!`, 'top', 3000);
        this.initializeSupplier();
        this.status = false;
        setTimeout(() => {
          window.location.reload();
        }, 3000);
      }, (error) => {
        const message = error.error.error;
        console.error('Error al actualizar el proveedor:', error);
        this.notification.showErrorToast(`Error al actualizar proveedor \n${message}`, 'top', 5000);
      });
    }
  }

  /**
   * Obtiene una lista de proveedores
   * Asigna una lista con proveedores a la lista suppliers
   */
  getSuppliers(): void {
    this.supplierService.getSuppliers().subscribe((supplierList) => {
      this.suppliers = supplierList;
    }, (error) => {
      console.log('Error no se encontró ningún proveedor', error);
    });
  }

  /**
   * Establece el proveedor por parámetro al proveedor seleccionado y dar un valor falso al status
   * para mostrar el formulario de agregar proveedor
   * @param supplier - Proveedor con los datos seleccionados para usar
   */
  getSelectedSupplier(supplier: Supplier): void {
    this.selectedSupplier = supplier;
    this.status = false;
  }

  /**
   * Abre el formulario para crear un nuevo proveedor.
   * Establece el valor al status del formulario para agregar.
   * Establece el formulario de actualización como false para ocultarlo.
   * Establece al proveedor seleccionado como nulo.
   * Inicializa los valores del nuevo proveedor.
   * @param statusClick - Indica si se debe abrir (true) o cerrar (false) el formulario.
   */
  openFormat(statusClick: boolean): void {
    this.status = statusClick;
    this.updateStatus = false;
    this.selectedSupplier = null;
    this.newSupplier = this.initializeSupplier();
  }

  /**
   * Abre el formulario para actualizar un proveedor.
   * Valida al proveedor seleccionado no se nulo y hace una copia de este.
   * Usa los datos de proveedor seleccionado para visualizar en el formulario.
   * Muestra el formulario y activa el modo actualización para el formulario.
   * Inicializa los valores del proveedor seleccionado.
   */
  openUpdateForm(): void {
    if (this.selectedSupplier) {
      this.newSupplier = { ...this.selectedSupplier };
      this.status = true;
      this.updateStatus = true;
      this.selectedSupplier = null;
    }
  }

  /**
   * Inicializa los valores del objeto proveedor.
   * @returns - Objeto vacío de proveedor con valores por defecto.
   */
  initializeSupplier(): Supplier {
    return this.newSupplier = {
      id: 0,
      name: '',
      email: '',
      rut: '',
      phone: '+569 '
    }
  }
}
