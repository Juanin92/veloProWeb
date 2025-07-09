import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../../services/communication/alert.service';
import { Alert } from '../../../models/entity/communication/alert';
import { TooltipService } from '../../../utils/tooltip.service';
import { UserPermissionsService } from '../../../services/permissions/user-permissions.service';
import { ErrorMessageService } from '../../../utils/error-message.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { AlertStatus } from '../../../models/enum/alert-status';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css'
})
export class AlertComponent implements OnInit{

  @Output() alertsUpdated = new EventEmitter<Alert[]>();
  alertList: Alert[] = [];
  alert: Alert = {
    id: 0,
    description: '',
    status: '',
    created: ''
  }
  
  constructor(
    private alertService: AlertService,
    private errorMessage: ErrorMessageService,
    private notification: NotificationService,
    protected permission: UserPermissionsService,
    private tooltip: TooltipService){}

  ngOnInit(): void {
    this.loadAlerts();
    this.tooltip.initializeTooltips();
  }
 
  loadAlerts(): void{
    this.alertService.getAlerts().subscribe({
      next:(list)=>{
        this.alertList = list;
        const filteredList = this.alertList.filter(a => a.status.includes('Alerta'));
        this.alertsUpdated.emit(filteredList);
      }
    });
  }

  changeStatusAlert(alert: Alert, action: number): void{
    const status = this.getStatusAction(action);
    this.alertService.handleStatusAlert(alert.id, status).subscribe({
      next:(response)=>{
        setTimeout(() => {
          this.loadAlerts();
        }, 3000);
      }, error: (error)=>{
        const message = this.errorMessage.errorMessageExtractor(error);
        this.notification.showErrorToast(message, 'top', 3000);
      }
    });
  }

  private getStatusAction(action: number): AlertStatus{
    if(action === 2) return AlertStatus.CHECKED;
    if(action === 3) return AlertStatus.PENDING;
    return AlertStatus.ALERT;
  }
}
