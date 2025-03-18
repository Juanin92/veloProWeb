import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportServiceService } from '../../../services/Report/report-service.service';
import { subDays, subMonths, subYears, format } from 'date-fns';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../../utils/notification-service.service';
import { ReportDTO } from '../../../models/DTO/Report/report-dto';
import { ChartService } from '../../../utils/chart.service';
import { ProductReportDTO } from '../../../models/DTO/Report/product-report-dto';
import { ReportPermissionsService } from '../../../services/Permissions/report-permissions.service';

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
  productReportList: ProductReportDTO[] = [];
  activeButton: string = '';
  total: string = '';
  highestDay: string = '';
  lowestDay: string = '';
  trend: string = '';

  constructor(
    private reportService: ReportServiceService,
    protected permission: ReportPermissionsService,
    private notification: NotificationService,
    private chartService: ChartService
  ) { }

  ngAfterViewInit(): void {
    this.chartService.initChart(this.chartElement);
  }

  ngOnInit(): void {
    this.buttonClicked = false;
  }

  /**
   * Maneja el clic en los botones de filtro de datos.
   * Reset los datos principales
   * @param button - Palabra clave que define el botón seleccionado
   */
  onButtonClick(button: string): void {
    this.resetData();
    this.activeButton = button;
    this.buttonClicked = true;
    this.selectedPeriod = '';
  }

  /**
   * Actualiza el período seleccionado del rango de fecha predeterminado 
   * establece las fechas de inicio y fin.
   * @param period Período seleccionado en días, meses o años.
   */
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

  /**
   * Realiza la llamada al servicio correspondiente según periodo seleccionado.
   * Válida rango de fechas si el modo es manual 
   * @param mode -  Modo de ejecución: 'automatic' o 'manual'.
   */
  callMethod(mode: string): void {
    if (mode === 'manual') {
      this.startDate = this.starDateInput;
      this.endDate = this.endDateInput;
      if (new Date(this.endDate).getTime() > Date.now()) {
        this.notification.showWarningToast('La fecha de fin no puede ser posterior a la fecha actual.', 'top', 3000);
        return;
      }
      if (this.startDate > this.endDate) {
        this.notification.showWarningToast('La fecha de inicio debe ser menor o igual que la fecha de fin.', 'top', 3000);
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
      case 'productSold':
        this.getProductsSold();
        break;
      case 'categoriesSold':
        this.getCategoriesSold();
        break;
      default:
        break;
    }
  }

  /**
   * Obtiene la cantidad de ventas diarias dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas.
   */
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

  /**
   * Obtiene el monto total de ventas diarias dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas con formato de peso CLP.
  */
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

  /**
   * Obtiene el monto promedio de ventas diarias dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas con formato de peso CLP.
  */
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

  /**
   * Obtiene las ganancias netas de ventas dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas con formato de peso CLP.
  */
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

  /**
   * Obtiene los productos más vendidos dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas con formato de peso CLP.
  */
  getProductsSold(): void {
    this.reportService.getMostProductSale(this.startDate, this.endDate).subscribe({
      next: (list) => {
        this.productReportList = list;
        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('productSale', 'Producto', 'Productos Más Vendidos');
      }, error: (error: Error) => {
        console.error(error.message);
      }
    });
  }

  /**
   * Obtiene las categorías más vendidas dentro del rango de fechas seleccionado.
   * Almacena los datos en una lista del key de un item.
   * Verifica si periodo seleccionado.
   * Actualiza el gráfico, Calcula las métricas con formato de peso CLP.
  */
  getCategoriesSold(): void {
    this.reportService.getMostCategorySale(this.startDate, this.endDate).subscribe({
      next: (list) => {
        this.productReportList = list;
        if (this.selectedPeriod === 'manual') {
          this.calculateSelectedPeriodManual();
        }
        this.updateChartData('categoriesSale', 'Categoría', 'Categorías Más Vendidas');
      }, error: (error: Error) => {
        console.error(error.message);
      }
    });
  }

  /**
   * Actualiza los datos del gráfico según la clave especificada, período seleccionado y tipo de lista
   * los agrupa y los envía al servicio de gráficos.
   * 
   * @param key Clave del valor (ejemplo: 'sale', 'sum', 'avg', 'profit', 'productSale').
   * @param legend Leyenda del gráfico.
   * @param title Título del gráfico.
   * @param formatFunction (Opcional) Función de formato .
   */
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
    if (this.productReportList.length > 0) {
      if (key === 'productSale') {
        this.chartService.updateChart(this.productReportList.map(item => item.total), this.productReportList.map(item => item.brand!), legend, title,);
      } else {
        this.chartService.updateChart(this.productReportList.map(item => item.total), this.productReportList.map(item => item.categoryName!), legend, title,);
      }
    }
  }

  /**
   * Agrupa los datos mensualmente sumando los valores por mes.
   * 
   * @param list Lista de reportes con fechas y valores.
   * @param key Clave del valor a agrupar.
   * @returns Lista de objetos con fecha formateada y valor total por mes.
   */
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

  /**
   * Agrupa los datos anualmente sumando los valores por año.
   * 
   * @param data Lista de reportes con fechas y valores.
   * @param key Clave del valor a agrupar.
   * @returns Lista de objetos con año y valor total.
   */
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

  /**
   * Calcula el período seleccionado manualmente en función del rango de fechas.
   * Ajusta `selectedPeriod` en función de la cantidad de días entre las fechas de inicio y fin.
   */
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

  /**
   * Calcula el resumen de ventas, incluyendo el total,
   * el día con más ventas y el día con menos ventas.
   *
   * @param valueKey Clave del valor a calcular (ejemplo: 'sale').
   * @param formatFunction (Opcional) Función de formato para los valores.
   */
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

  /**
   * Calcula la tendencia de ventas de los datos disponibles.
   * Divide el período en dos mitades y evalúa si las ventas han aumentado o disminuido.
   *
   * @param key Clave del valor a evaluar para determinar la tendencia.
   */
  private calculateTrend(key: string): void {
    if (this.reportList.length > 0) {
      let periodDays = 30;

      //Determina el número de días por el periodo seleccionado
      switch (this.selectedPeriod) {
        case '30': periodDays = 30; break;
        case '60': periodDays = 60; break;
        case '90': periodDays = 90; break;
        case '6': periodDays = 180; break;
        case '1': periodDays = 365; break;
        default: break;
      }

      //Calculas las fechas y las divide para el análisis
      const endDate = new Date(this.endDate);
      const startDate = new Date(this.startDate);
      const halfPeriodDate = new Date(endDate);
      halfPeriodDate.setDate(endDate.getDate() - Math.floor(periodDays / 2));

      //Calcula las ventas reciente
      const recentSales = this.reportList
        .filter(item => new Date(item.date) > halfPeriodDate)
        .reduce((sum, item) => sum + item.values[key], 0);
      //Calcula las ventas anteriores
      const previousSales = this.reportList
        .filter(item => new Date(item.date) > startDate && new Date(item.date) <= halfPeriodDate)
        .reduce((sum, item) => sum + item.values[key], 0);
      //Determina la tendencia en crecimiento o disminución
      const growth = recentSales - previousSales;
      this.trend = growth > 0 ? 'increase' : growth < 0 ? 'decrease' : '';
    } else {
      this.trend = '';
    }
  }

  /**
   * Formatea los valores de un objeto según su clave para visualizarlos en la tabla HTML.
   *
   * @param obj Objeto con claves y valores numéricos.
   * @returns Lista de objetos con clave y valor formateado si es necesario.
   */
  getFormattedValues(obj: { [key: string]: number }): { key: string; value: string | number }[] {
    const formattedKeys = ['sum', 'avg', 'profit']; // Claves que requieren formato CLP
    return Object.keys(obj).map(key => ({
      key: key,
      value: formattedKeys.includes(key) ? this.formatCLP(obj[key]) : obj[key]
    }));
  }

  /**
   * Restablece los datos del reporte y el gráfico.
   */
  private resetData(): void {
    this.reportList.splice(0, this.reportList.length);
    this.productReportList.splice(0, this.productReportList.length);
    this.chartService.resetChart(this.chartElement);
    this.total = '';
    this.highestDay = '';
    this.lowestDay = '';
    this.startDate = '';
    this.endDate = '';
    this.trend = '';
    this.activeButton = '';
  }

  /**
   * Formatea una fecha en formato "DD-MM-YYYY".
   *
   * @param dateString Cadena de fecha.
   * @returns Fecha formateada o 'Invalid Date' si la entrada no es válida.
   */
  private formatDate(dateString: string): string {

    const date = new Date(dateString + 'T00:00:00Z');

    if (isNaN(date.getTime())) {
      console.error(`Invalid Date: ${dateString}`);
      return 'Invalid Date';
    }

    return `${date.getUTCDate().toString().padStart(2, '0')}-${(date.getUTCMonth() + 1).toString().padStart(2, '0')}-${date.getUTCFullYear()}`;
  }

  /**
   * Formatea un valor numérico como moneda CLP (Pesos chilenos).
   *
   * @param value Valor numérico a formatear.
   * @returns Cadena con el valor formateado en CLP.
   */
  private formatCLP(value: number): string {
    return '$' + new Intl.NumberFormat('es-CL', { maximumFractionDigits: 0 }).format(value);
  }
}