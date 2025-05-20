import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { SupplierValidator } from '../../../validation/supplier-validator';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { PurchasePermissionsService } from '../../../services/Permissions/purchase-permissions.service';
import { SupplierForm } from '../../../models/Entity/Purchase/supplier-form';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit {

  suppliers: Supplier[] = [];
  supplierInfo: SupplierForm;
  selectedSupplier: Supplier | null = null;
  status: boolean = false; //Estado para mostrar el formulario de creación
  updateStatus: boolean = false; //Estado para mostrar el formulario de actualización
  validator = SupplierValidator;
  touchedFields: Record<string, boolean> = {};

  constructor(
    private supplierService: SupplierService,
    private notification: NotificationService,
    private errorMessage: ErrorMessageService,
    protected permission: PurchasePermissionsService) {
    this.supplierInfo = this.initializeSupplierForm();
  }

  ngOnInit(): void {
    this.getSuppliers();
  }

  /**
   * Obtiene una lista de proveedores
   * Asigna una lista con proveedores a la lista suppliers
   */
  getSuppliers(): void {
    this.supplierService.getSuppliers().subscribe(
      (supplierList) => this.suppliers = supplierList,
    );
  }

  /**
   * Agregar un nuevo proveedor.
   * Valida el formulario y si es correcto, llama al servicio para agregar el proveedor.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la lista de proveedores.
   */
  createSupplier(): void {
    if (this.validator.validateForm(this.supplierInfo)) {
      this.supplierService.createSupplier(this.supplierInfo).subscribe({
          next: (response) => {
          this.notification.showSuccessToast(`${response.message} \n${this.supplierInfo.name}`, 'top', 3000);
          this.initializeSupplierForm();
          this.getSuppliers();
          this.openCreateFormat(false);
        },error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`Error: \n${message}`, 'top', 5000);
        }
      });
    } else {
      this.notification.showWarning('Formulario incompleto', 'Por favor, complete correctamente todos los campos obligatorios.');
    }
  }

  /**
   * Actualizar datos de un proveedor.
   * Valida el formulario y si es correcto, llama al servicio para actualizar el proveedor.
   * Muestra notificaciones dependiendo el estado de la acción y refresca la lista de proveedores.
   */
  updateSupplier(): void {
    if (this.validator.validateForm(this.supplierInfo)) {
      console.log('proveedor actualizado: ', this.supplierInfo);
      this.supplierService.updateSupplier(this.supplierInfo).subscribe({
        next:(response) => {
          this.notification.showSuccessToast(`${this.supplierInfo.name}: \n${response.message}`, 'top', 3000);
          this.initializeSupplierForm();
          this.status = false;
          this.getSuppliers();
        },error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(`Error: \n${message}`, 'top', 5000);
        }
      });
    }
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
  openCreateFormat(statusClick: boolean): void {
    this.status = statusClick;
    this.updateStatus = false;
    this.selectedSupplier = null;
    this.supplierInfo = this.initializeSupplierForm();
    this.touchedFields = {};
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
      this.supplierInfo = { ...this.selectedSupplier };
      this.status = true;
      this.updateStatus = true;
      this.selectedSupplier = null;
    }
  }

  /**
   * Inicializa los valores del objeto proveedor.
   * @returns - Objeto vacío de proveedor con valores por defecto.
   */
  initializeSupplierForm(): SupplierForm {
    return {
      name: '',
      email: '',
      rut: '',
      phone: '+569 '
    }
  }
}
