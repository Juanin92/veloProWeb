import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SaleDetail } from '../../../models/Entity/Sale/sale-detail';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { ModalService } from '../../../utils/modal.service';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { DispatchService } from '../../../services/sale/dispatch.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { SaleDetailDTO } from '../../../models/DTO/sale-detail-dto';
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';
import { DispatchRequest } from '../../../models/Entity/Sale/dispatch-request';
import { ErrorMessageService } from '../../../utils/error-message.service';

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
  dispatch: DispatchRequest = {
      address: '',
      comment: '',
      customer: '',
      detailSaleDTOList: null,
  }
  
  constructor(
      private dispatchService: DispatchService,
      protected permission: DispatchPermissionsService,
      private notification: NotificationService,
      private errorMessage: ErrorMessageService,
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

  processNewDispatch(dispatch: DispatchRequest): void{
    this.saleDetailDTOList = this.saleDetailList.map(saleDetail => {
      return {
        id: saleDetail.id,
        idProduct: saleDetail.product.id,
        quantity: saleDetail.quantity
      };
    });
    dispatch.detailSaleDTOList = this.saleDetailDTOList;
    this.dispatchService.createDispatch(dispatch).subscribe({
      next:(response)=>{
        this.notification.showSuccessToast(response.message, 'top', 3000);
        this.clearDispatchModal();
        this.dispatchCreated.emit(true);
        this.modalService.closeModal();
      },error: (error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }
  
  clearDispatchModal(): void{
    this.dispatch.address = '';
    this.dispatch.comment = '';
    this.dispatch.customer = '';
    this.selectedDispatch = null;
    this.totalSum = 0;
  }
}
