import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { SaleDetail } from '../../../models/Entity/Sale/sale-detail';
import { SaleDetailDTO } from '../../../models/DTO/sale-detail-dto';
import { TooltipService } from '../../../utils/tooltip.service';
import { NotificationService } from '../../../utils/notification-service.service';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css'
})
export class DispatchComponent implements OnInit{

  @Input() saleDetailList: SaleDetail[] = [];
  @Output() dispatchUpdated = new EventEmitter<Dispatch[]>();
  dispatchList: Dispatch[] = [];
  saleDetailDTOList: SaleDetailDTO[] = [];
  saleDetailDispatchList: DetailSaleRequestDTO[] = [];
  selectedDispatch: Dispatch | null = null;
  totalSum: number = 0;
  dispatch: Dispatch = {
    id: 0,
    trackingNumber: '',
    status: '',
    address: '',
    comment: '',
    customer: '',
    created: '',
    deliveryDate: '',
    detailSaleDTOS: null,
  }

  constructor(
    private dispatchService: DispatchService,
    private tooltip: TooltipService,
    private notification: NotificationService){}

  ngOnInit(): void {
    this.tooltip.initializeTooltips();
    this.getDispatches();
  }

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
        this.dispatchUpdated.emit(list.filter(
          dispatch => dispatch.status === 'En Preparación' || dispatch.status === 'En Ruta'));
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

  createNewDispatch(dispatch: Dispatch): void{
    this.saleDetailDTOList = this.saleDetailList.map(saleDetail => {
      return {
        id: saleDetail.id,
        idProduct: saleDetail.product.id,
        quantity: saleDetail.quantity
      };
    });
    dispatch.detailSaleDTOS = this.saleDetailDTOList;
    this.dispatchService.createDispatch(dispatch).subscribe({
      next:(response)=>{
        const message = response.message;
        this.notification.showSuccessToast(message, 'top', 3000);
        this.resetModal();
      },error: (error)=>{
        const message = error.error?.error || error.error?.message || error?.error;
        console.log("ERROR: ", message);
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  resetModal(): void{
    this.dispatch.address = '';
    this.dispatch.comment = '';
    this.dispatch.customer = '';
    this.selectedDispatch = null;
    this.totalSum = 0;
  }

  newDetailDispatch(): DetailSaleRequestDTO{
    return {
      descriptionProduct: 'Despacho',
      quantity: 1,
      price: 1000,
      customer: '',
      notification: '',
      ticketStatus: true
    }
  }
}
