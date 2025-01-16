import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ProductService } from '../../../services/Product/product.service';
import { ProductHelperService } from '../../../services/Product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { Product } from '../../../models/Entity/Product/product.model';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ModalService } from '../../../utils/modal.service';

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.css'
})
export class UpdateProductComponent implements OnChanges{

  @Input() selectedProduct: Product;
  @Output() productUpdated = new EventEmitter<void>();
  validator = ProductValidator;
  stockChanged: boolean = false;
  originalStock: number;

  constructor(
    private productService: ProductService,
    private helper: ProductHelperService,
    private notification: NotificationService,
    public modalService: ModalService) {
    this.selectedProduct = helper.createEmptyProduct();
    this.originalStock = this.selectedProduct.stock;
  }

  /**
   * Se ejecuta cuando decorador presenta un cambio.
   * Actualiza el stock del producto seleccionado a la variable para manejar un nuevo valor del stock
   * @param changes - Cambio detectado
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedProduct']) {
      this.originalStock = changes['selectedProduct'].currentValue.stock;
    }
  }

  /**
   * Valida si el cambio de propiedad stock ha cambiado o no, para poder visualizar una nueva vista.
   * Si el parámetro de estado es falso, entonces a la variable queda como false
   * Si el stock actual y el stock original son distintos, entonces a la variable queda como true
   * Si no se ha hecho ningún cambio entonces se procede a llamar al método actualizar
   * @param status - Estado que debería mostrar la variable
   * @var stockChanged - Indica si hubo cambio del valor de stock (true) o no (false).
   * @returns - Retornar sin hacer nada más
   */
  stockChangeValidation(status: boolean): void{
    if (!status) {
      this.stockChanged = false;
      return;
    }
    if (this.selectedProduct.stock !== this.originalStock) {
      this.stockChanged = true;
    }
    if (!this.stockChanged) {
      this.updateProduct(); 
    }
  }

  /**
   * Actualiza los datos de un producto seleccionado
   * Valida que el producto no sea nulo y valida su formulario
   * Se crea una copia del producto seleccionado a una nueva variable
   * Si todo es correcto, llama el servicio para actualizar al cliente
   * Muestra notificaciones dependiendo el estado de la acción y emite un evento para refrescar todos los productos.
   * Cambio los valores de las variables reiniciando esos valores
   */
  updateProduct(): void{
    if (this.selectedProduct && this.validator.validateForm(this.selectedProduct)) {
      const updatedProduct = {...this.selectedProduct};
      this.productService.updateProduct(updatedProduct).subscribe((response) =>{
        console.log('Se actualizo el producto: ', updatedProduct);
        this.notification.showSuccessToast(`Se actualizo el producto ${updatedProduct.description} correctamente`, 'top', 3000);
        this.productUpdated.emit();
        this.originalStock = this.selectedProduct.stock;
        this.stockChanged = false;
        this.modalService.closeModal();
      }, (error) => {
        const message = error.error?.message || error.error?.error;
        this.notification.showErrorToast(`Error al actualizar producto \n${message}`, 'top', 5000);
        console.log('Error al actualizar producto: ', message);
      });
    }
  }
}
