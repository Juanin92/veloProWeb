import { Component, EventEmitter, OnInit, Output} from '@angular/core';
import { DispatchService } from '../../../services/sale/dispatch.service';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { TooltipService } from '../../../utils/tooltip.service';
import { PaymentDispatchComponent } from "../payment-dispatch/payment-dispatch.component";
import { DispatchModalComponent } from "../dispatch-modal/dispatch-modal.component";
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';
import { DispatchStatus } from '../../../models/enum/dispatch-status';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { NotificationService } from '../../../utils/notification-service.service';

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
  status = DispatchStatus;

  constructor(
    private dispatchService: DispatchService,
    protected permission: DispatchPermissionsService,
    private errorMessage: ErrorMessageService,
    private notification: NotificationService,
    private tooltip: TooltipService){}

  ngOnInit(): void {
    this.tooltip.initializeTooltips();
    this.loadCurrentDispatches();
  }

  loadCurrentDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        const filteredList = list.filter(
          dispatch => dispatch.status === DispatchStatus.PREPARING || dispatch.status === DispatchStatus.IN_ROUTE);
        this.dispatchList = filteredList;
        this.dispatchUpdated.emit(filteredList);
      }
    });
  }

  loadSaleDetailsForDispatch(dispatchValue: Dispatch): void{
    this.dispatchService.getDetailSale(dispatchValue.id).subscribe({
      next:(list) =>{
        this.saleDetailDispatchList = list;
        this.selectedDispatch = dispatchValue;
        this.saleDetailDispatchList.push(this.initializeSaleRequest());
        this.saleDetailDispatchList.map(value => this.totalSum += (value.price * value.quantity));
      }
    });
  }

  handleStatusDispatch(dispatch: Dispatch, action: DispatchStatus): void{
    this.dispatchService.handleStatusDispatch(dispatch.id, action).subscribe({
      next:(response)=>{
        this.loadCurrentDispatches();
      },error:(error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  initializeSaleRequest(): DetailSaleRequestDTO{
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
