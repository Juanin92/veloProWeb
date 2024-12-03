import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Costumer } from '../../../../models/Costumer/costumer.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-update-modal',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './update-modal.component.html',
  styleUrl: './update-modal.component.css'
})
export class UpdateModalComponent {
  @Input() selectedCostumer: Costumer | null = null;
  @Output() costumerUpdated = new EventEmitter<Costumer>();

  updateCostumer() {
    if (this.selectedCostumer) {
      this.costumerUpdated.emit(this.selectedCostumer);
    }
  }
}
