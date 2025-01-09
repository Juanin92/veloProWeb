import { Injectable } from '@angular/core';
import * as bootstrap from 'bootstrap';

@Injectable({
  providedIn: 'root'
})
export class TooltipService {

  initializeTooltips(): void {
      const toolTipElementList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
      toolTipElementList.forEach((element) => {
        new bootstrap.Tooltip(element);
      });
    }
}
