import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DailySaleCountDTO } from '../../../models/DTO/Report/daily-sale-count-dto';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';


@Component({
  selector: 'app-report',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportComponent implements OnInit {

  buttonClicked: boolean = false;
  selectedPeriod: string = '';
  startDate: string = '';
  endDate: string = '';
  dailySaleCountList: DailySaleCountDTO[] = [];

  constructor(private reportService: ReportServiceService) { }

  ngOnInit(): void {
    this.buttonClicked = false;
  }

  onPeriodChange(period: string): void {
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
  }

  getDailySale(): void{
    console.log('Fecha inicio', this.startDate);
    console.log('Fecha final', this.endDate);
    this.reportService.getDailySale(this.startDate, this.endDate).subscribe((list)=>{
      this.dailySaleCountList = list;
    }, (error: Error) => {
      console.error(error.message); 
    });
  }

}
