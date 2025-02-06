import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ReportDTO } from '../../../models/DTO/Report/report-dto';
import ApexCharts from 'apexcharts';
import { ChartService } from '../../../utils/chart.service';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportComponent implements OnInit, AfterViewInit {

  @ViewChild('chart') chartElement!: ElementRef;
  buttonClicked: boolean = false;
  selectedPeriod: string = '';
  startDate: string = '';
  starDateInput: string = '';
  endDate: string = '';
  endDateInput: string = '';
  reportList: ReportDTO[] = [];
  activeButton: string = '';
  total: string = '';
  highestDay: string = '';
  lowestDay: string = '';

  constructor(
    private reportService: ReportServiceService,
    private notification: NotificationService,
    private chartService: ChartService
  ) { }


  ngAfterViewInit(): void {
    this.chartService.initChart(this.chartElement);
  }

  ngOnInit(): void {
    this.buttonClicked = false;
  }

  onButtonClick(button: string): void {
    this.resetData();
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
    if (mode === 'manual') {
      this.startDate = this.starDateInput;
      this.endDate = this.endDateInput;
      if (this.startDate > this.endDate) {
        this.notification.showWarningToast('La fecha de inicio debe ser menor que la fecha de fin.', 'top', 3000);
        return;
      }
      this.selectedPeriod = 'manual';
    }

    if (this.startDate === '' || this.endDate === '') {
      this.notification.showWarningToast('Fechas no disponibles', 'top', 3000);
      return;
    }

    switch (this.activeButton) {
      case 'totalSale':
        this.getDailySale();
        break;
      case 'amountSale':
        this.getTotalSaleDaily();
        break;
      case 'averageSale':
        this.getAverageTotalSaleDaily(); 
        break;
      case 'totalEarnings':
        this.getEarningSale();
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

  getDailySale(): void {
    this.reportService.getDailySale(this.startDate, this.endDate).subscribe({
      next: (list) => {
        this.reportList = (list as any[]).map(item => ({
          date: item.date,
          values: { ['sale']: item.sale }
        }));
        const total = this.reportList.reduce((sum, item) => sum + item.values['sale'], 0);
        this.total = total >= 1 ? total + ' ventas' : total + ' venta';
        const highestDay = this.reportList.reduce((max, item) => (item.values['sale'] > max.values['sale'] ? item : max), this.reportList[0]);
        this.highestDay = highestDay ? this.formatDate(highestDay.date) + '\n(' + highestDay.values['sale'] + ' ventas)' : 'Sin datos';
        const lowestDay = this.reportList.reduce((max, item) => (item.values['sale'] < max.values['sale'] ? item : max), this.reportList[0]);
        this.lowestDay = lowestDay ? this.formatDate(lowestDay.date) + '\n(' + lowestDay.values['sale'] + ' ventas)' : 'Sin datos';

        if (this.selectedPeriod === 'manual') {
          const start = new Date(this.startDate);
          const end = new Date(this.endDate);
          const diffTime = Math.abs(end.getTime() - start.getTime());
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          if (diffDays <= 60) {
            this.selectedPeriod = '30';
          } else if (diffDays <= 90) {
            this.selectedPeriod = '90';
          } else if (diffDays <= 180) {
            this.selectedPeriod = '6';
          } else if (diffDays <= 365) {
            this.selectedPeriod = '1';
          } else {
            this.selectedPeriod = 'annual';
          }
        }
        if(this.reportList.length > 0){
          switch (this.selectedPeriod) {
            case '30':
              this.chartService.updateChart(this.reportList.map(item => item.values['sale']), this.reportList.map(item => this.formatDate(item.date)), "Ventas", "Cantidad de Ventas diarias");
              break;
            case '60':
            case '90':
            case '6':
            case '1':
              const groupedByMonth = this.groupByMonth(this.reportList, 'sale');
              this.chartService.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), "Ventas", "Cantidad de Ventas Mensual");
              break;
            case 'annual':
              const groupedByYear = this.groupByYear(this.reportList, 'sale');
              this.chartService.updateChart(groupedByYear.map(item => item.values['sale']), groupedByYear.map(item => this.formatDate(item.date)), "Ventas", "Cantidad de Ventas por años");
              break;
            default:
              break;
          }
        }
      }, error: (error: Error) => {
        console.error(error.message);
      }
    });
  }

  getTotalSaleDaily(): void {
      this.reportService.getTotalSaleDaily(this.startDate, this.endDate).subscribe({
        next: (list) => {
          this.reportList = (list as any[]).map(item => ({
            date: item.date,
            values: { ['sum']: item.sum }
          }));
        const total = this.reportList.reduce((sum, item) => sum + item.values['sum'], 0);
        this.total = this.formatCLP(total);
        const highestDay = this.reportList.reduce((max, item) => (item.values['sum'] > max.values['sum'] ? item : max), this.reportList[0]);
        this.highestDay = highestDay
        ? this.formatDate(highestDay.date) + '\n' + this.formatCLP(highestDay.values['sum']) : 'Sin datos';
        const lowestDay = this.reportList.reduce((max, item) => (item.values['sum'] < max.values['sum'] ? item : max), this.reportList[0]);
        this.lowestDay = lowestDay
        ? this.formatDate(lowestDay.date) + '\n' + this.formatCLP(lowestDay.values['sum']) : 'Sin datos';  

          if (this.selectedPeriod === 'manual') {
            const start = new Date(this.startDate);
            const end = new Date(this.endDate);
            const diffTime = Math.abs(end.getTime() - start.getTime());
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            if (diffDays <= 60) {
              this.selectedPeriod = '30';
            } else if (diffDays <= 90) {
              this.selectedPeriod = '90';
            } else if (diffDays <= 180) {
              this.selectedPeriod = '6';
            } else if (diffDays <= 365) {
              this.selectedPeriod = '1';
            } else {
              this.selectedPeriod = 'annual';
            }
          }
          if(this.reportList.length > 0){
            switch (this.selectedPeriod) {
              case '30':
                this.chartService.updateChart(this.reportList.map(item =>  item.values['sum']), this.reportList.map(item => this.formatDate(item.date)), "$", "Monto total de Ventas diarias", this.formatCLP);
                break;
              case '60':
              case '90':
              case '6':
              case '1':
                const groupedByMonth = this.groupByMonth(this.reportList, 'sum');
                this.chartService.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), "$", "Monto total de Ventas mensual", this.formatCLP);
                break;
              case 'annual':
                const groupedByYear = this.groupByYear(this.reportList, 'sum');
                this.chartService.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), "$", "Monto total de Ventas por año", this.formatCLP);
                break;
              default:
                break;
            }
          }
        }, error: (error: Error) => {
          console.error(error.message);
        }
      });
  }

  getAverageTotalSaleDaily(): void {
      this.reportService.getAverageTotalSaleDaily(this.startDate, this.endDate).subscribe({
        next: (list) => {
          this.reportList = (list as any[]).map(item => ({
            date: item.date,
            values: { ['avg']: item.avg }
          }));
        const total = this.reportList.reduce((sum, item) => sum + item.values['avg'], 0);
        this.total = this.formatCLP(total);
        const highestDay = this.reportList.reduce((max, item) => (item.values['avg'] > max.values['avg'] ? item : max), this.reportList[0]);
        this.highestDay = highestDay ? this.formatDate(highestDay.date) + '\n' + this.formatCLP(highestDay.values['avg']) : 'Sin datos';
        const lowestDay = this.reportList.reduce((max, item) => (item.values['avg'] < max.values['avg'] ? item : max), this.reportList[0]);
        this.lowestDay = lowestDay ? this.formatDate(lowestDay.date) + '\n' + this.formatCLP(lowestDay.values['avg']) : 'Sin datos';
  
          if (this.selectedPeriod === 'manual') {
            const start = new Date(this.startDate);
            const end = new Date(this.endDate);
            const diffTime = Math.abs(end.getTime() - start.getTime());
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            if (diffDays <= 60) {
              this.selectedPeriod = '30';
            } else if (diffDays <= 90) {
              this.selectedPeriod = '90';
            } else if (diffDays <= 180) {
              this.selectedPeriod = '6';
            } else if (diffDays <= 365) {
              this.selectedPeriod = '1';
            } else {
              this.selectedPeriod = 'annual';
            }
          }
          if(this.reportList.length > 0){
            switch (this.selectedPeriod) {
              case '30':
                this.chartService.updateChart(this.reportList.map(item => item.values['avg']), this.reportList.map(item => this.formatDate(item.date)), "$", "Monto promedio de Ventas diarias", this.formatCLP);
                break;
              case '60':
              case '90':
              case '6':
              case '1':
                const groupedByMonth = this.groupByMonth(this.reportList, 'avg');
                this.chartService.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), "$", "Monto promedio de Ventas mensual", this.formatCLP);
                break;
              case 'annual':
                const groupedByYear = this.groupByYear(this.reportList, 'avg');
                this.chartService.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), "$", "Monto promedio de Ventas por año", this.formatCLP);
                break;
              default:
                break;
            }
          }
        }, error: (error: Error) => {
          console.error(error.message);
        }
      });
  }

  getEarningSale(): void {
    this.reportService.getEarningSale(this.startDate, this.endDate).subscribe({
      next: (list) => {
        this.reportList = (list as any[]).map(item => ({
          date: item.date,
          values: { ['profit']: item.profit }
        }));
      const total = this.reportList.reduce((sum, item) => sum + item.values['profit'], 0);
      this.total = this.formatCLP(total);
      const highestDay = this.reportList.reduce((max, item) => (item.values['profit'] > max.values['profit'] ? item : max), this.reportList[0]);
      this.highestDay = highestDay ? this.formatDate(highestDay.date) + '\n' + this.formatCLP(highestDay.values['profit']) : 'Sin datos';
      const lowestDay = this.reportList.reduce((max, item) => (item.values['profit'] < max.values['profit'] ? item : max), this.reportList[0]);
      this.lowestDay = lowestDay ? this.formatDate(lowestDay.date) + '\n' + this.formatCLP(lowestDay.values['profit']) : 'Sin datos';

        if (this.selectedPeriod === 'manual') {
          const start = new Date(this.startDate);
          const end = new Date(this.endDate);
          const diffTime = Math.abs(end.getTime() - start.getTime());
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          if (diffDays <= 60) {
            this.selectedPeriod = '30';
          } else if (diffDays <= 90) {
            this.selectedPeriod = '90';
          } else if (diffDays <= 180) {
            this.selectedPeriod = '6';
          } else if (diffDays <= 365) {
            this.selectedPeriod = '1';
          } else {
            this.selectedPeriod = 'annual';
          }
        }
        if(this.reportList.length > 0){
          switch (this.selectedPeriod) {
            case '30':
              this.chartService.updateChart(this.reportList.map(item => item.values['profit']), this.reportList.map(item => this.formatDate(item.date)), "$", "Ganancias de Ventas diarias", this.formatCLP);
              break;
            case '60':
            case '90':
            case '6':
            case '1':
              const groupedByMonth = this.groupByMonth(this.reportList, 'profit');
              this.chartService.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), "$", "Ganancias de Ventas mensual", this.formatCLP);
              break;
            case 'annual':
              const groupedByYear = this.groupByYear(this.reportList, 'profit');
              this.chartService.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), "$", "Ganancias de Ventas por año", this.formatCLP);
              break;
            default:
              break;
          }
        }
      }, error: (error: Error) => {
        console.error(error.message);
      }
    });
}

  groupByMonth(list: ReportDTO[], key: string): { date: string, value: number }[] {
    const grouped: { [key: string]: number } = {};

    list.forEach(item => {
        const date = new Date(item.date);
        const monthYear = `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getFullYear()}`; 

        if (!grouped[monthYear]) {
            grouped[monthYear] = 0;
        }
        grouped[monthYear] += item.values[key];
    });

    return Object.entries(grouped).map(([date, value]) => ({ date, value }));
}

  groupByYear(data: ReportDTO[], key: string): any[] {
    const groupedData: { [key: string]: number } = {};

    data.forEach(item => {
      const year = new Date(item.date).getFullYear();
      if (!groupedData[year]) {
        groupedData[year] = 0;
      }
      groupedData[year] += item.values[key];
    });

    return Object.entries(groupedData).map(([year, total]) => ({
      year,
      total
    }));
  }

  public getFormattedValues(obj: { [key: string]: number }): { key: string; value: string | number }[] {
    const formattedKeys = ['sum', 'avg', 'profit']; // Claves que requieren formato CLP
    return Object.keys(obj).map(key => ({
      key: key,
      value: formattedKeys.includes(key) ? this.formatCLP(obj[key]) : obj[key]
    }));
  }

  resetData(): void{
    this.reportList.splice(0, this.reportList.length);
    this.chartService.resetChart(this.chartElement);
    this.total = '';
    this.highestDay = '';
    this.lowestDay = '';
    this.startDate = '';
    this.endDate = '';
  }

  private formatDate(dateString: string): string {
    
    const date = new Date(dateString + 'T00:00:00Z'); 

    if (isNaN(date.getTime())) {
        console.error(`Invalid Date: ${dateString}`);
        return 'Invalid Date';
    }

    return `${date.getUTCDate().toString().padStart(2, '0')}-${(date.getUTCMonth() + 1).toString().padStart(2, '0')}-${date.getUTCFullYear()}`;
  }

  private formatCLP(value: number): string {
    return '$' + new Intl.NumberFormat('es-CL', { maximumFractionDigits: 0 }).format(value);
  }   
}