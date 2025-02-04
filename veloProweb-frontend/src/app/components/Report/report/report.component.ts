import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ReportDTO } from '../../../models/DTO/Report/report-dto';


@Component({
  selector: 'app-report',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportComponent implements OnInit {

  buttonClicked: boolean = false;
  selectedPeriod: string = '';
  startDate: string = '';
  starDateInput: string = '';
  endDate: string = '';
  endDateInput: string = '';
  dailySaleCountList: ReportDTO[] = [];
  activeButton: string = '';

  constructor(
    private reportService: ReportServiceService,
    private notification: NotificationService
  ) { }

  ngOnInit(): void {
    this.buttonClicked = false;
  }

  onButtonClick(button: string): void{
    this.activeButton = button;
    this.buttonClicked = true;
    this.selectedPeriod = '';
  }

  onPeriodChange(period: string): void {
    this.starDateInput = '';
    this.endDateInput = '';
    this.selectedPeriod = period;
    const now = new Date();
    let past: Date = now;

    switch (period) {
      case '30':
        past = subDays(now, 31);
        break;
      case '60':
        past = subDays(now, 61);
        break;
      case '90':
        past = subDays(now, 91);
        break;
      case '6':
        past = subMonths(now, 6);
        break;
      case '1':
        past = subYears(now, 1);
        break;
      default:
        break;
    }
    this.startDate = format(past, 'yyyy-MM-dd');
    this.endDate = format(now, 'yyyy-MM-dd');
    this.callMethod('automatic');
  }

  callMethod(mode: string): void {
    if(mode === 'manual'){
      this.startDate = format(this.starDateInput, 'yyyy-MM-dd');
      this.endDate = format(this.endDateInput, 'yyyy-MM-dd');
      if (this.startDate > this.endDate) {
        this.notification.showWarningToast('La fecha de inicio debe ser menor que la fecha de fin.','top', 3000);
        return;
      }
    }
    
    if (this.startDate === '' || this.endDate === '') {
      this.notification.showWarningToast('Fechas no disponibles','top', 3000);
      return;
    }

    switch (this.activeButton) {
      case 'totalSale':
        this.getDailySale();
        break;
      case 'totalAmount':
        // this.getTotalAmount(); 
        break;
      case 'averageSale':
        // this.getAverageSale(); 
        break;
      case 'totalEarnings':
        // this.getTotalEarnings(); 
        break;
      case 'productsSold':
        // this.getProductsSold(); 
        break;
      case 'categoriesSold':
        // this.getCategoriesSold();
        break;
      default:
        break;
    }
  }

  getDailySale(): void{
    this.reportService.getDailySale(this.startDate, this.endDate).subscribe({
      next:(list)=>{
        this.dailySaleCountList = list;
      }, error: (error: Error) => {
      console.error(error.message); 
      }
    });
  }

}
