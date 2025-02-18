import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css'
})
export class DispatchComponent implements OnInit{

  @Output() dispatchUpdated = new EventEmitter<Dispatch[]>();
  dispatchList: Dispatch[] = [];
  dispatch: Dispatch = {
    id: 0,
    trackingNumber: '',
    status: '',
    address: '',
    comment: '',
    customer: '',
    created: '',
    deliveryDate: '',
    saleDetail: null,
  }

  constructor(private dispatchService: DispatchService){}

  ngOnInit(): void {
    this.getDispatches();
  }

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
      }, error: (error)=>{
        console.log('Error al obtener despachos', error.error?.error);
      }
    });
  }

  createNewDispatch(): void{
    this.dispatchService.createDispatch(this.dispatch).subscribe();
  }
}
