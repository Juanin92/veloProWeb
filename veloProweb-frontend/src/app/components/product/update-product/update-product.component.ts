import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ProductService } from '../../../services/product/product.service';
import { ProductHelperService } from '../../../services/product/product-helper.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { Product } from '../../../models/entity/product/product';
import { ProductValidator } from '../../../validation/product-validator';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ModalService } from '../../../utils/modal.service';
import { ProductPermissionsService } from '../../../services/permissions/product-permissions.service';
import { ProductMapperService } from '../../../mapper/product-mapper.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { ProductUpdateForm } from '../../../models/entity/product/product-update-form';

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.css',
})
export class UpdateProductComponent implements OnChanges {
  @Input() selectedProduct: Product;
  @Output() productUpdated = new EventEmitter<void>();
  validator = ProductValidator;
  updatedProduct: ProductUpdateForm;
  stockChanged: boolean = false;
  originalStock: number;
  commentInput: string = '';

  constructor(
    private productService: ProductService,
    private helper: ProductHelperService,
    private mapper: ProductMapperService,
    private notification: NotificationService,
    public modalService: ModalService,
    protected permission: ProductPermissionsService,
    private errorMessage: ErrorMessageService
  ) {
    this.selectedProduct = helper.createEmptyProduct();
    this.updatedProduct = helper.createEmptyProductUpdateForm();
    this.originalStock = this.selectedProduct.stock;
  }

  /**
   * Se ejecuta cuando decorador presenta un cambio.
   * Actualiza el stock del producto seleccionado a la variable para manejar un nuevo valor del stock
   * @param changes - Cambio detectado
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedProduct'] && this.selectedProduct) {
      this.originalStock = changes['selectedProduct'].currentValue.stock;
      this.selectedProduct = JSON.parse(JSON.stringify(changes['selectedProduct'].currentValue));
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
  stockChangeValidation(status: boolean): void {
    if (!status) {
      this.stockChanged = false;
      return;
    }
    if (this.selectedProduct.stock !== this.originalStock) {
      this.stockChanged = true;
    }
    if (!this.stockChanged) {
      this.updateProductInfo();
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
  updateProductInfo(): void {
    if (
      this.selectedProduct &&
      this.validator.validateForm(this.selectedProduct)
    ) {
      const productCopied = { ...this.selectedProduct };
      this.updatedProduct = this.mapper.mapProductToUpdate(productCopied);
      this.updatedProduct.comment = this.commentInput;
      this.productService.updateProduct(this.updatedProduct).subscribe({
        next: (response) => {
          this.notification.showSuccessToast(
            `${response.message} <br>${this.updatedProduct.description}`,
            'top',
            3000
          );
          this.productUpdated.emit();
          this.originalStock = this.selectedProduct.stock;
          this.stockChanged = false;
          this.modalService.closeModal();
        },
        error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          this.notification.showErrorToast(
            `Error al actualizar producto \n${message}`,
            'top',
            5000
          );
        },
      });
    }
  }
}
