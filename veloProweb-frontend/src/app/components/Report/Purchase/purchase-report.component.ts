import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { PurchaseService } from '../../../services/Purchase/purchase.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TooltipService } from '../../../utils/tooltip.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { ExcelService } from '../../../utils/excel.service';
import { ReportPermissionsService } from '../../../services/Permissions/report-permissions.service';
import { PurchaseResponse } from '../../../models/Entity/Purchase/purchase-response';
import { PurchaseDetailResponse } from '../../../models/Entity/Purchase/purchase-detail-response';

@Component({
  selector: 'app-purchase-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-report.component.html',
  styleUrl: './purchase-report.component.css'
})
export class PurchaseReportComponent implements OnInit, AfterViewInit {

  purchaseList: PurchaseResponse[] = [];
  filteredPurchaseList: PurchaseResponse[] = [];
  purchaseDetailList: PurchaseDetailResponse[] = [];
  textFilter: string = '';
  startDate: string = '';
  finalDate: string = '';
  sortDate: boolean = true;
  sortTotal: boolean = true;
  sortPosition: boolean = true;

  constructor(
    private purchaseService: PurchaseService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    private renderer: Renderer2,
    private excelService: ExcelService,
    protected permission: ReportPermissionsService) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.loadPurchases();
  }

  loadPurchases(): void{
    this.purchaseService.getAllPurchases().subscribe((list) => {
        this.purchaseList = list;
        this.filteredPurchaseList = list;
      });
  }

  /**
   * Obtiene información de los detalles de compra de una compra seleccionada
   * @param details - Detalles de la compra seleccionada
   */
  loadPurchaseDetailInfo(details: PurchaseDetailResponse[]): void {
    this.purchaseDetailList = details
  }

  /**
   * Filtra las compras por rango de fechas ingresadas por el usuario.
   * Muestra un mensaje de advertencia si las fechas no son válidas.
   */
  dateFilterPurchases(): void {
    if (this.startDate && this.finalDate) {
      const startDate = new Date(this.startDate);
      const finalDate = new Date(this.finalDate);

      if (startDate < finalDate) {
        this.filteredPurchaseList = this.purchaseList.filter(purchase => {
          const saleDate = new Date(purchase.date);
          return saleDate >= startDate && saleDate <= finalDate;
        });
      } else {
        this.notification.showWarningToast('La fecha de inicio debe ser menor que la fecha final.', "top", 2000);
      }
    }
  }

  /**
   *Limpia los campos de filtro de fechas y restablece la lista de compras filtradas.
   */
  cleanInputFilterDates(): void {
    this.startDate = '';
    this.finalDate = '';
    this.filteredPurchaseList = this.purchaseList;
  }

  /**
   * Descarga de datos de la lista (reportes) en un archivo excel
   * Transforma la lista filtrada a un formato y datos necesarios a mostrar
   */
  downloadExcel(): void{
    const transformedData = this.filteredPurchaseList.map(item => ({
      fecha: item.date,
      documento: item.documentType + '-' + item.document,
      iva: item.iva,
      total: item.purchaseTotal,
      proveedor: item.supplier,
    }));
    this.excelService.generateExcel(transformedData, 'Reporte-Compras');
  }

  toggleSortDate(): void{
    this.sortDate = !this.sortDate;
    this.filteredPurchaseList.sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return this.sortDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortTotal(): void{
    this.sortTotal = !this.sortTotal;
    this.filteredPurchaseList.sort((a, b) => {
      const dateA = a.purchaseTotal;
      const dateB = b.purchaseTotal;
      return this.sortTotal ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortPosition(): void{
    this.filteredPurchaseList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  /**
   *Filtra las compras según el texto ingresado en el campo de búsqueda.
   *Busca coincidencias en el documento, tipo de documento o nombre del proveedor.
   */
  searchFilterPurchases(): void {
    if (this.textFilter.trim() === '') {
      this.filteredPurchaseList = this.purchaseList;
    } else {
      this.filteredPurchaseList = this.purchaseList.filter(purchase =>
        purchase.document.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        purchase.documentType.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        purchase.supplier.toLowerCase().includes(this.textFilter.toLowerCase()));
    }
  }
}
