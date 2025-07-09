import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DispatchService } from '../../../services/sale/dispatch.service';
import { Dispatch } from '../../../models/entity/sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TooltipService } from '../../../utils/tooltip.service';
import { PaymentDispatchComponent } from '../payment-dispatch/payment-dispatch.component';
import { DispatchModalComponent } from '../dispatch-modal/dispatch-modal.component';
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';
import { DispatchStatus } from '../../../models/enum/dispatch-status';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { DispatchHelperService } from '../../../services/sale/dispatch-helper.service';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PaymentDispatchComponent,
    DispatchModalComponent,
  ],
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css',
})
export class DispatchComponent implements OnInit {
  @Output() dispatchUpdated = new EventEmitter<Dispatch[]>();
  dispatchList: Dispatch[] = [];
  selectedDispatch: Dispatch;
  totalSum: number = 0;
  status = DispatchStatus;

  constructor(
    private dispatchService: DispatchService,
    protected permission: DispatchPermissionsService,
    private errorMessage: ErrorMessageService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    protected helper: DispatchHelperService
  ) {
    this.selectedDispatch = helper.initializeDispatch();
  }

  ngOnInit(): void {
    this.tooltip.initializeTooltips();
    this.loadCurrentDispatches();
  }

  loadCurrentDispatches(): void {
    this.dispatchService.getDispatches().subscribe({
      next: (list) => {
        const filteredList = list.filter((item) => item.status !== 'DELIVERED');
        this.dispatchList = filteredList;
        this.dispatchUpdated.emit(filteredList);
      },
    });
  }

  getSaleDetailsForDispatch(dispatchDetails: Dispatch): void {
    this.selectedDispatch = dispatchDetails;
    this.totalSum = this.selectedDispatch.saleDetails.reduce(
      (sum, value) => sum + value.price * value.quantity,
      0
    );
  }

  handleStatusDispatch(dispatch: Dispatch, action: DispatchStatus): void {
    const statusKey = Object.keys(DispatchStatus).find(
      (key) => DispatchStatus[key as keyof typeof DispatchStatus] === action
    );
    if (!statusKey)
      return this.notification.showWarningToast('Estado de despacho inválido', 'top', 3000);
    this.dispatchService
      .handleStatusDispatch(dispatch.id, statusKey as DispatchStatus)
      .subscribe({
        next: () => this.loadCurrentDispatches(),
        error: (error) => this.notification.showErrorToast(
          this.errorMessage.errorMessageExtractor(error), 'top', 3000)
      });
  }
}
