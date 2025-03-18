import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../../services/User/alert.service';
import { AlertModel } from '../../../models/Entity/alert-model';
import { TooltipService } from '../../../utils/tooltip.service';
import { UserPermissionsService } from '../../../services/Permissions/user-permissions.service';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css'
})
export class AlertComponent implements OnInit{

  @Output() alertsUpdated = new EventEmitter<AlertModel[]>();
  alertList: AlertModel[] = [];
  alert: AlertModel = {
    id: 0,
    description: '',
    status: '',
    created: ''
  }
  
  constructor(
    private alertService: AlertService,
    protected permission: UserPermissionsService,
    private tooltip: TooltipService){}

  ngOnInit(): void {
    this.getAlerts();
    this.tooltip.initializeTooltips();
  }
 
  getAlerts(): void{
    this.alertService.getAlerts().subscribe({
      next:(list)=>{
        this.alertList = list;
        const filteredList = this.alertList.filter(a => a.status.includes('Alerta'));
        this.alertsUpdated.emit(filteredList);
      },error: (error)=>{
        console.log('Error al obtener alertas: ', error.error?.error);
      }
    });
  }

  changeStatusAlert(alert: AlertModel, action: number): void{
    this.alertService.handleStatusAlert(alert, action).subscribe({
      next:(response)=>{
        const message = response.message;
        console.log('Ok: ', message);
        alert.status = action === 2 ? 'Revisado' : 'Pendiente';
        setTimeout(() => {
          this.getAlerts();
        }, 3000);
      }, error: (error)=>{
        const message = error.error?.message || error.error?.error;
        console.log('Error: ', message);
      }
    });
  }
}
