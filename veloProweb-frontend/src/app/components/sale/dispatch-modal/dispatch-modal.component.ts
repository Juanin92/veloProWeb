import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SaleDetail } from '../../../models/Entity/Sale/sale-detail';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { ModalService } from '../../../utils/modal.service';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { SaleDetailDTO } from '../../../models/DTO/sale-detail-dto';

@Component({
  selector: 'app-dispatch-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch-modal.component.html',
  styleUrl: './dispatch-modal.component.css'
})
export class DispatchModalComponent implements OnInit, OnChanges{

  @Input() selectedDispatchDetail: Dispatch | null = null;
  @Input() saleDetailDispatchList: DetailSaleRequestDTO[] = []; 
  @Output() dispatchCreated = new EventEmitter<boolean>();
  @Input() saleDetailList: SaleDetail[] = [];
  selectedDispatch: Dispatch | null = null;
  saleDetailDTOList: SaleDetailDTO[] = [];
  totalSum: number = 0;
  dispatch: Dispatch = {
      id: 0,
      trackingNumber: '',
      status: '',
      address: '',
      comment: '',
      customer: '',
      hasSale: false,
      created: '',
      deliveryDate: '',
      detailSaleDTOS: null,
  }
  
  constructor(
      private dispatchService: DispatchService,
      private notification: NotificationService,
      public modalService: ModalService){}

  ngOnInit(): void {
    this.modalService.openModal();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.selectedDispatch = this.selectedDispatchDetail;
    if (changes['saleDetailDispatchList'] && changes['saleDetailDispatchList'].currentValue) {
      this.totalSum = this.saleDetailDispatchList.reduce((sum, value) => sum + (value.price * value.quantity), 0);
    }
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
        this.dispatchCreated.emit(true);
        this.modalService.closeModal();
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
}
