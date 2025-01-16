import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  private activeModalId: string | null = null;

  /**
   * Abre un modal específico configurando su ID como el modal activo.
   * @param modalId ID del modal a abrir
   */
  openModal(modalId: string): void {
    this.activeModalId = modalId;
  }

  /**
   * Cierra el modal actualmente activo.
   */
  closeModal(): void {
    this.activeModalId = null;
  }

  /**
   * Verifica si un modal con el ID especificado está activo (abierto).
   * @param modalId ID del modal a verificar
   * @returns `true` si el modal está activo, `false` en caso contrario
   */
  isModalOpen(modalId: string): boolean {
    return this.activeModalId === modalId;
  }
}
