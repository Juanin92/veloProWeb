import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { Sale } from '../../../models/entity/sale/sale';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaleService } from '../../../services/sale/sale.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { TooltipService } from '../../../utils/tooltip.service';
import { ExcelService } from '../../../utils/excel.service';
import { PdfService } from '../../../utils/pdf.service';
import { ReportPermissionsService } from '../../../services/permissions/report-permissions.service';
import { SaleDetailResponse } from '../../../models/entity/sale/sale-detail-response';

@Component({
  selector: 'app-sale-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sale-report.component.html',
  styleUrl: './sale-report.component.css'
})
export class SaleReportComponent implements OnInit, AfterViewInit {

  saleReportList: Sale[] = [];
  saleList: Sale[] = [];
  filteredSaleList: Sale[] = [];
  saleDetails: SaleDetailResponse[] = [];
  saleSelected: Sale | null = null;
  customerSale: string = '';
  notificationSale: string = '';
  statusTicket: boolean = true;
  textFilter: string = '';
  startDate: string = '';
  finalDate: string = '';
  hasDispatchDetailsSale: boolean = false;
  sortDate: boolean = true;
  sortTotal: boolean = true;
  sortPosition: boolean = true;

  constructor(
    private saleService: SaleService,
    protected permission: ReportPermissionsService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    private renderer: Renderer2,
    private excelService: ExcelService,
    private pdfService: PdfService
  ) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.loadSales();
  }

  /**
   * Obtiene una lista de ventas
   */
  loadSales(): void {
    this.saleService.getAllSales().subscribe({
      next: (list) => {
        this.saleReportList = list;
        this.filteredSaleList = list;
      },
    });
  }

  loadSaleDetails(sale: Sale): void{
    this.saleSelected = sale;
    this.hasDispatchDetailsSale = sale.saleDetails[0].hasDispatch;
  }

  /**
   * Filtra ventas por un rango de fechas
   * Muestra un mensaje de advertencia si el rango no es valido
   */
  dateFilterSales(): void {
    if (this.startDate && this.finalDate) {
      const startDate = new Date(this.startDate);
      const finalDate = new Date(this.finalDate);

      if (startDate < finalDate) {
        this.filteredSaleList = this.saleList.filter(sale => {
          const saleDate = new Date(sale.date);
          return saleDate >= startDate && saleDate <= finalDate;
        });
      } else {
        this.notification.showWarningToast('La fecha de inicio debe ser menor que la fecha final.', "top", 2000);
      }
    }
    this.tooltip.initializeTooltips();
  }

  /**
   * Limpia los campos de filtro de fechas y restablece la lista de ventas filtradas.
   */
  cleanInputFilterDates(): void {
    this.startDate = '';
    this.finalDate = '';
    this.filteredSaleList = this.saleReportList;
    this.tooltip.initializeTooltips();
  }

  /**
   * Imprimir una boleta de venta.
   * primero obtiene los datos detallados de la venta y luego genera un PDF
   * @param sale - Venta a imprimir
   */
  printSale(sale: Sale): void{
    this.pdfService.generatePDF(sale);
  }

  /**
   * Descarga de datos de la lista (reportes) en un archivo excel
   * Transforma la lista filtrada a un formato y datos necesarios a mostrar
   */
  downloadExcel(): void{
    const transformedData = this.filteredSaleList.map(item => ({
      documento: item.document,
      fecha: item.date,
      MétodoPago: item.paymentMethod,
      iva: item.tax,
      total: item.totalSale,
      observación: item.comment
    }));
    this.excelService.generateExcel(transformedData, 'Reporte-ventas');
  }

  toggleSortDate(): void{
    this.sortDate = !this.sortDate;
    this.filteredSaleList.sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return this.sortDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortTotal(): void{
    this.sortTotal = !this.sortTotal;
    this.filteredSaleList.sort((a, b) => {
      const dateA = a.totalSale;
      const dateB = b.totalSale;
      return this.sortTotal ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortPosition(): void{
    this.filteredSaleList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  /**
   * Filtra las ventas según el texto ingresado en el campo de búsqueda.
   * Busca coincidencias en el método de pago, documento o si la venta está anulada.
   */
  searchFilterSales(): void {
    if (this.textFilter.trim() === '') {
      this.filteredSaleList = this.saleList;
    } else {
      this.filteredSaleList = this.saleList.filter(sale =>
        sale.paymentMethod?.toString().toLowerCase().includes(this.textFilter.toLowerCase()) ||
        sale.document.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        (this.textFilter.toLowerCase() === 'anulado' && sale.comment.toLowerCase().includes('anulado'))
      );
    }
    this.tooltip.initializeTooltips();
  }
}
