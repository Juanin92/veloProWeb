import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../../services/User/alert.service';
import { AlertModel } from '../../../models/Entity/alert-model';

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
  
  constructor(private alertService: AlertService){}

  ngOnInit(): void {
    this.getAlerts();
  }

  
  getAlerts(): void{
    this.alertService.getAlerts().subscribe({
      next:(list)=>{
        this.alertList = list;
        this.alertsUpdated.emit(this.alertList);
      },error: (error)=>{
        console.log('Error al obtener alertas: ', error.error?.error);
      }
    });
  }
}
