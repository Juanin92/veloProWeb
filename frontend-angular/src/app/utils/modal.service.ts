import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  private isVisible: boolean = false;

  /**
   * Abre un modal 
   */
  openModal(): void {
    this.isVisible = true;
  }

  /**
   * Cierra el modal actualmente activo.
   */
  closeModal(): void {
    this.isVisible = false;
  }

  /**
   * Retornar el estado de visibilidad
   * @returns - estado de visibilidad
   */
  isModalVisible(): boolean {
    return this.isVisible;
  }
}
