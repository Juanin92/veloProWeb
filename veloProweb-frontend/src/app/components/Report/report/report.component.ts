import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ReportDTO } from '../../../models/DTO/Report/report-dto';
import ApexCharts from 'apexcharts';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportComponent implements OnInit, AfterViewInit {

  @ViewChild('chart') chartElement!: ElementRef;
  chart!: ApexCharts;
  buttonClicked: boolean = false;
  selectedPeriod: string = '';
  startDate: string = '';
  starDateInput: string = '';
  endDate: string = '';
  endDateInput: string = '';
  dailySaleCountList: ReportDTO[] = [];
  activeButton: string = '';
  total: string = '';
  highestDay: string = '';
  lowestDay: string = '';

  constructor(
    private reportService: ReportServiceService,
    private notification: NotificationService
  ) { }


  ngAfterViewInit(): void {
    this.initChart();
  }

  ngOnInit(): void {
    this.buttonClicked = false;
  }

  onButtonClick(button: string): void {
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
      this.startDate = format(this.starDateInput, 'yyyy-MM-dd');
      this.endDate = format(this.endDateInput, 'yyyy-MM-dd');
      if (this.startDate > this.endDate) {
        this.notification.showWarningToast('La fecha de inicio debe ser menor que la fecha de fin.', 'top', 3000);
        return;
      }
      this.selectedPeriod = 'manual'
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

  getDailySale(): void {
    const key = 'sale';
    this.reportService.getDailySale(this.startDate, this.endDate).subscribe({
      next: (list) => {
        this.dailySaleCountList = (list as any[]).map(item => ({
          date: item.date,
          values: { [key]: item.sale }
        }));
        console.log('lista:',list);
        console.log('listaDTO :',this.dailySaleCountList);
        console.log('key ', key);
        const total = this.dailySaleCountList.reduce((sum, item) => sum + item.values[key], 0);
        this.total = total >= 1 ? total + ' ventas' : total + ' venta';
        const highestDay = this.dailySaleCountList.reduce((max, item) => (item.values[key] > max.values[key] ? item : max), this.dailySaleCountList[0]);
        this.highestDay = highestDay ? this.formatDate(highestDay.date) + '\n(' + highestDay.values[key] + ' ventas)' : 'Sin datos';
        const lowestDay = this.dailySaleCountList.reduce((max, item) => (item.values[key] < max.values[key] ? item : max), this.dailySaleCountList[0]);
        this.lowestDay = lowestDay ? this.formatDate(lowestDay.date) + '\n(' + lowestDay.values[key] + ' ventas)' : 'Sin datos';
        console.log('mejor:', this.highestDay);
        console.log('total:', this.total);
        console.log('total const:', total);
        console.log('peor:', this.lowestDay);

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
        if(this.dailySaleCountList.length > 0){
          switch (this.selectedPeriod) {
            case '30':
              this.updateChart(this.dailySaleCountList.map(item => item.values[key]), this.dailySaleCountList.map(item => this.formatDate(item.date)), this.dailySaleCountList, "Ventas", "Cantidad de Ventas diarias");
              break;
            case '60':
            case '90':
            case '6':
            case '1':
              const groupedByMonth = this.groupByMonth(this.dailySaleCountList, key);
              this.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), groupedByMonth.map(item => ({
                date: item.date,
                values: { [key]: item.value }})), "Ventas", "Cantidad de Ventas Mensual");
              break;
            case 'annual':
              const groupedByYear = this.groupByYear(this.dailySaleCountList, key);
              this.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), groupedByYear, "Ventas", "Cantidad de Ventas por año");
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
      const key = 'sum';
      this.reportService.getTotalSaleDaily(this.startDate, this.endDate).subscribe({
        next: (list) => {
          this.dailySaleCountList = (list as any[]).map(item => ({
            date: item.date,
            values: { [key]: item.sum }
          }));
          console.log('lista:',list);
        console.log('listaDTO :',this.dailySaleCountList);
        console.log('key ', key);
        const total = this.dailySaleCountList.reduce((sum, item) => sum + item.values[key], 0);
        this.total = '$' + total;
        const highestDay = this.dailySaleCountList.reduce((max, item) => (item.values[key] > max.values[key] ? item : max), this.dailySaleCountList[0]);
        this.highestDay = highestDay ? this.formatDate(highestDay.date) + '\n($' + highestDay.values[key] + ')' : 'Sin datos';
        const lowestDay = this.dailySaleCountList.reduce((max, item) => (item.values[key] < max.values[key] ? item : max), this.dailySaleCountList[0]);
        this.lowestDay = lowestDay ? this.formatDate(lowestDay.date) + '\n($' + lowestDay.values[key] + ')' : 'Sin datos';
        console.log('mejor:', this.highestDay);
        console.log('total:', this.total);
        console.log('total const:', total);
        console.log('peor:', this.lowestDay);
  
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
          if(this.dailySaleCountList.length > 0){
            switch (this.selectedPeriod) {
              case '30':
                this.updateChart(this.dailySaleCountList.map(item => item.values[key]), this.dailySaleCountList.map(item => this.formatDate(item.date)), this.dailySaleCountList, "$", "Monto total de Ventas diarias");
                break;
              case '60':
              case '90':
              case '6':
              case '1':
                const groupedByMonth = this.groupByMonth(this.dailySaleCountList, key);
                this.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), groupedByMonth.map(item => ({
                  date: item.date,
                  values: { [key]: item.value }})), "$", "Monto total de Ventas mensual");
                break;
              case 'annual':
                const groupedByYear = this.groupByYear(this.dailySaleCountList, key);
                this.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), groupedByYear, "$", "Monto total de Ventas por año");
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

  initChart(): void {
    const options = {
        series: [{ name: "", data: [] }],
        chart: { type: "bar", height: "100%", width: "100%" },
        plotOptions: {
            bar: {
                distributed: true,
                horizontal: false,
                columnWidth: "50%"
            }
        },
        colors: ['#008FFB', '#00E396', '#FEB019'], // Colores personalizados para las barras
        xaxis: {
            categories: [],
            labels: {
                style: {
                    colors: '#fff', // Color blanco para los labels
                    fontWeight: 'bold' // Texto en negrita
                }
            }
        },
        dataLabels: {
            enabled: true,
            style: {
                colors: ['#fff'] // Color blanco para los labels
            }
        },
        title: {
            text: "",
            align: "center",
            style: {
                color: '#fff' // Color blanco para el título
            }
        },
        tooltip: {
            theme: 'dark', // Tema oscuro para el tooltip
            style: {
                fontSize: '14px',
                background: '#333', // Color de fondo del tooltip
                color: '#fff' // Color del texto en el tooltip
            },
            x: {
                show: true,
                formatter: undefined
            },
            y: {
                formatter: (val: number) => `Ventas: ${val}`,
                title: {
                    formatter: () => ''
                }
            }
        },
        legend: {
            show: false
        }
    };

    this.chart = new ApexCharts(this.chartElement.nativeElement, options);
    this.chart.render();
  }

  updateChart(serie: number[], categories: string[] = [], list: ReportDTO[], legend: string, title: string): void {
    this.chart.updateOptions({
      series: [{ name: legend, data: serie }],
      xaxis: { categories: categories },
      title: { text: title, align: "center" }
    });
  }

  private formatDate(dateString: string): string {
    
    const date = new Date(dateString + 'T00:00:00Z'); 

    if (isNaN(date.getTime())) {
        console.error(`Invalid Date: ${dateString}`);
        return 'Invalid Date';
    }

    return `${date.getUTCDate().toString().padStart(2, '0')}-${(date.getUTCMonth() + 1).toString().padStart(2, '0')}-${date.getUTCFullYear()}`;
  }
}
