import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ReportDTO } from '../../../models/DTO/Report/report-dto';
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
  trend: string = '';

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
        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('sale', 'Ventas', 'Cantidad de Ventas');
        this.calculateSummary('sale');
        this.calculateTrend('sale');
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
        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('sum', '$', 'Monto total de Ventas', this.formatCLP);
        this.calculateSummary('sum', this.formatCLP);
        this.calculateTrend('sum');
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

        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('avg', '$', 'Monto Promedio de Ventas', this.formatCLP);
        this.calculateSummary('avg', this.formatCLP);
        this.calculateTrend('avg');
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

        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('profit', '$', 'Ganancias de Ventas', this.formatCLP);
        this.calculateSummary('profit', this.formatCLP);
        this.calculateTrend('profit');
      }, error: (error: Error) => {
        console.error(error.message);
      }
    });
  }

  private updateChartData(key: string, legend: string, title: string, formatFunction?: (value: number) => string): void {
    if (this.reportList.length > 0) {
      switch (this.selectedPeriod) {
        case '30':
          this.chartService.updateChart(this.reportList.map(item => item.values[key]), this.reportList.map(item => this.formatDate(item.date)), legend, title + ' Diarias', formatFunction);
          break;
        case '60':
        case '90':
        case '6':
        case '1':
          const groupedByMonth = this.groupByMonth(this.reportList, key);
          this.chartService.updateChart(groupedByMonth.map(item => item.value), groupedByMonth.map(item => item.date), legend, title + ' Mensual', formatFunction);
          break;
        case 'annual':
          const groupedByYear = this.groupByYear(this.reportList, key);
          this.chartService.updateChart(groupedByYear.map(item => item.total), groupedByYear.map(item => item.year), legend, title + ' Anual', formatFunction);
          break;
        default:
          break;
      }
    }
  }

  private groupByMonth(list: ReportDTO[], key: string): { date: string, value: number }[] {
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

  private groupByYear(data: ReportDTO[], key: string): any[] {
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

  private calculateSelectedPeriodManual(): void {
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

  private calculateSummary(valueKey: string, formatFunction?: (value: number) => string): void {
    const total = this.reportList.reduce((sum, item) => sum + item.values[valueKey], 0);
    this.total = formatFunction ? formatFunction(total) : `${total} ${total === 1 ? 'venta' : 'ventas'}`;

    const highestDay = this.reportList.reduce((max, item) => (item.values[valueKey] > max.values[valueKey] ? item : max), this.reportList[0]);
    const lowestDay = this.reportList.reduce((min, item) => (item.values[valueKey] < min.values[valueKey] ? item : min), this.reportList[0]);

    if (highestDay) {
      const formattedValue = formatFunction ? formatFunction(highestDay.values[valueKey]) : highestDay.values[valueKey];
      this.highestDay = valueKey === 'sale'
        ? `${this.formatDate(highestDay.date)}\n(${formattedValue} ${formattedValue === 1 ? 'venta' : 'ventas'})`
        : `${this.formatDate(highestDay.date)}\n${formattedValue}`;
    } else {
      this.highestDay = 'Sin datos';
    }

    if (lowestDay) {
      const formattedValue = formatFunction ? formatFunction(lowestDay.values[valueKey]) : lowestDay.values[valueKey];
      this.lowestDay = valueKey === 'sale'
        ? `${this.formatDate(lowestDay.date)}\n(${formattedValue} ${formattedValue === 1 ? 'venta' : 'ventas'})`
        : `${this.formatDate(lowestDay.date)}\n${formattedValue}`;
    } else {
      this.lowestDay = 'Sin datos';
    }
  }

  private calculateTrend(key: string): void {
    if (this.reportList.length > 0) {
      let periodDays = 30;

      switch (this.selectedPeriod) {
        case '30': periodDays = 30; break;
        case '60': periodDays = 60; break;
        case '90': periodDays = 90; break;
        case '6': periodDays = 180; break;
        case '1': periodDays = 365; break;
        default: break;
      }
      const endDate = new Date(this.endDate);
      const startDate = new Date(this.startDate);
      const halfPeriodDate = new Date(endDate);
      halfPeriodDate.setDate(endDate.getDate() - Math.floor(periodDays / 2));

      const recentSales = this.reportList
        .filter(item => new Date(item.date) > halfPeriodDate)
        .reduce((sum, item) => sum + item.values[key], 0);

      const previousSales = this.reportList
        .filter(item => new Date(item.date) > startDate && new Date(item.date) <= halfPeriodDate)
        .reduce((sum, item) => sum + item.values[key], 0);

      const growth = recentSales - previousSales;
      this.trend = growth > 0 ? 'increase' : growth < 0 ? 'decrease' : '';
    } else {
      this.trend = '';
    }
  }

  getFormattedValues(obj: { [key: string]: number }): { key: string; value: string | number }[] {
    const formattedKeys = ['sum', 'avg', 'profit']; // Claves que requieren formato CLP
    return Object.keys(obj).map(key => ({
      key: key,
      value: formattedKeys.includes(key) ? this.formatCLP(obj[key]) : obj[key]
    }));
  }

  private resetData(): void {
    this.reportList.splice(0, this.reportList.length);
    this.chartService.resetChart(this.chartElement);
    this.total = '';
    this.highestDay = '';
    this.lowestDay = '';
    this.startDate = '';
    this.endDate = '';
    this.trend = '';
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