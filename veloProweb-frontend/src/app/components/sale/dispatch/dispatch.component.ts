import { Component, EventEmitter, OnInit, Output} from '@angular/core';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { TooltipService } from '../../../utils/tooltip.service';
import { PaymentDispatchComponent } from "../payment-dispatch/payment-dispatch.component";
import { DispatchModalComponent } from "../dispatch-modal/dispatch-modal.component";
import { DispatchPermissionsService } from '../../../services/Permissions/dispatch-permissions.service';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule, PaymentDispatchComponent, DispatchModalComponent],
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css'
})
export class DispatchComponent implements OnInit{

  @Output() dispatchUpdated = new EventEmitter<Dispatch[]>();
  dispatchList: Dispatch[] = [];
  saleDetailDispatchList: DetailSaleRequestDTO[] = [];
  selectedDispatch: Dispatch | null = null;
  totalSum: number = 0;

  constructor(
    private dispatchService: DispatchService,
    protected permission: DispatchPermissionsService,
    private tooltip: TooltipService){}

  ngOnInit(): void {
    this.tooltip.initializeTooltips();
    this.getDispatches();
  }

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        const filteredList = list.filter(
          dispatch => dispatch.status === 'En Preparación' || dispatch.status === 'En Ruta');
        this.dispatchList = filteredList;
        this.dispatchUpdated.emit(filteredList);
      }, error: (error)=>{
        console.log('Error al obtener despachos', error.error?.error);
      }
    });
  }

  getSaleDetailToDispatch(dispatchValue: Dispatch): void{
    this.dispatchService.getDetailSale(dispatchValue.id).subscribe({
      next:(list) =>{
        this.saleDetailDispatchList = list;
        this.selectedDispatch = dispatchValue;
        this.saleDetailDispatchList.push(this.newDetailDispatch());
        this.saleDetailDispatchList.map(value => this.totalSum += (value.price * value.quantity));
      },error: (error)=>{
        console.log('Error al obtener detalle del despacho, ', error?.error);
      }
    });
  }

  handleStatusDispatch(dispatch: Dispatch, action: number): void{
    this.dispatchService.handleStatusDispatch(dispatch.id, action).subscribe({
      next:(response)=>{
        const message = response.message;
        console.log('Cambio de estado del despacho, ', message);
        this.getDispatches();
      },error:(error)=>{
        const message = error.error?.message || error.error?.error || error?.error;
        console.log('Error estado del despacho, ', message);
      }
    });
  }

  newDetailDispatch(): DetailSaleRequestDTO{
    return {
      descriptionProduct: 'Despacho',
      quantity: 1,
      price: 1000,
      tax: 0,
      customer: '',
      notification: '',
      ticketStatus: true,
      hasDispatch: true
    }
  }
}
