import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Costumer } from '../../../../models/Customer/customer.model';
import { PaymentStatus } from '../../../../models/enum/payment-status.enum';

@Component({
  selector: 'app-add-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './add-modal.component.html',
  styleUrl: './add-modal.component.css'
})
export class AddModalComponent {
  @Output() costumerAdded = new EventEmitter<any>();
  

  saveCostumer() {
    this.costumerAdded.emit(this.costumerAdded);
  }
}
