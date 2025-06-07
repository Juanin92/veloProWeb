import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SaleDetail } from '../../../models/entity/sale/sale-detail';
import { Dispatch } from '../../../models/entity/sale/dispatch';
import { ModalService } from '../../../utils/modal.service';
import { DispatchService } from '../../../services/sale/dispatch.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';
import { DispatchRequest } from '../../../models/entity/sale/dispatch-request';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { SaleDetailResponse } from '../../../models/entity/sale/sale-detail-response';
import { DispatchHelperService } from '../../../services/sale/dispatch-helper.service';
import { SaleDetailRequest } from '../../../models/entity/sale/sale-detail-request';
import { SaleMapperService } from '../../../mapper/sale-mapper.service';

@Component({
  selector: 'app-dispatch-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch-modal.component.html',
  styleUrl: './dispatch-modal.component.css'
})
export class DispatchModalComponent implements OnInit{

  @Input() dispatchSelectedDetail: Dispatch;
  @Input() totalSum: number = 0;
  @Output() dispatchCreated = new EventEmitter<boolean>();
  @Input() saleDetailList: SaleDetail[] = [];
  saleDetailDispatchList: SaleDetailResponse[] = [];
  selectedDispatch: Dispatch;
  saleDetailRequests: SaleDetailRequest[] = [];
  // totalSum: number = 0;
  dispatchRequest: DispatchRequest;
  
  constructor(
      private dispatchService: DispatchService,
      protected permission: DispatchPermissionsService,
      private mapper: SaleMapperService,
      private notification: NotificationService,
      private errorMessage: ErrorMessageService,
      protected helper: DispatchHelperService,
      public modalService: ModalService){
        this.dispatchSelectedDetail = helper.initializeDispatch();
        this.selectedDispatch = helper.initializeDispatch();
        this.dispatchRequest = helper.initializeDispatchRequest();
      }

  ngOnInit(): void {
    this.modalService.openModal();
  }

  processNewDispatch(dispatch: DispatchRequest): void{
    this.saleDetailRequests = this.mapper.mapToSaleDetailRequest(this.saleDetailList);
    dispatch.saleDetails = this.saleDetailRequests;
    console.log('despacho nuevo: ',  dispatch);
    // this.dispatchService.createDispatch(dispatch).subscribe({
    //   next:(response)=>{
    //     this.notification.showSuccessToast(response.message, 'top', 3000);
    //     this.clearDispatchModal();
    //     this.dispatchCreated.emit(true);
    //     this.modalService.closeModal();
    //   },error: (error)=>{
    //     const message = this.errorMessage.errorMessageExtractor(error);
    //     this.notification.showErrorToast(message, 'top', 3000);
    //   }
    // });
  }
  
  clearDispatchModal(): void{
    // this.dispatchSelectedDetail = this.helper.initializeDispatch();
    // this.selectedDispatch = this.helper.initializeDispatch();
    // this.dispatchRequest = this.helper.initializeDispatchRequest();
    // this.totalSum = 0;
  }
}
