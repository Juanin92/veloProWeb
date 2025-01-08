import { Component } from '@angular/core';
import { Purchase } from '../../../models/Entity/Purchase/purchase';
import { PurchaseValidator } from '../../../validation/purchase-validator';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.css'
})
export class PurchaseComponent {
  
  purchase: Purchase;
  validator = PurchaseValidator;

  constructor(){
    this.purchase = this.createEmptyPurchase();
  }

  createEmptyPurchase(): Purchase{
    return this.purchase ={ 
      id: 0,
      document: '',
      documentType: '',
      tax: 0,
      purchaseTotal: 0,
      date: '',
      supplier: null
    }
  }
}
