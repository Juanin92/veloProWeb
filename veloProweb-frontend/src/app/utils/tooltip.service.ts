import { Injectable } from '@angular/core';
import * as bootstrap from 'bootstrap';

@Injectable({
  providedIn: 'root'
})
export class TooltipService {

  initializeTooltips(): void {
    const tooltipTriggerList: HTMLElement[] = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach((tooltipTriggerEl: HTMLElement) => {
      // Destruir todas las instancias anteriores si existen
      let existingInstance = bootstrap.Tooltip.getInstance(tooltipTriggerEl);
      if (existingInstance) {
        existingInstance.dispose();
      }

      // Inicializar el nuevo tooltip
      const tooltipInstance = new bootstrap.Tooltip(tooltipTriggerEl, {
        trigger: 'manual'
      });

      // Evitar duplicación de eventos
      if (!tooltipTriggerEl.hasAttribute('data-tooltip-initialized')) {
        tooltipTriggerEl.addEventListener('mouseenter', () => {
          const instance = bootstrap.Tooltip.getInstance(tooltipTriggerEl);
          if (instance) {
            instance.show();
          }
        });

        tooltipTriggerEl.addEventListener('mouseleave', () => {
          const instance = bootstrap.Tooltip.getInstance(tooltipTriggerEl);
          if (instance) {
            setTimeout(() => {
              if (!tooltipTriggerEl.matches(':hover')) {
                instance.hide();
              }
            }, 2000);
          }
        });

        tooltipTriggerEl.setAttribute('data-tooltip-initialized', 'true');
      }
    });
  }
}
