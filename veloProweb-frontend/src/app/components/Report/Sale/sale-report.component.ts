import { AfterViewChecked, AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { Sale } from '../../../models/Entity/Sale/sale';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaleService } from '../../../services/Sale/sale.service';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { NotificationService } from '../../../utils/notification-service.service';
import { TooltipService } from '../../../utils/tooltip.service';
import { ExcelService } from '../../../utils/excel.service';

@Component({
  selector: 'app-sale-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sale-report.component.html',
  styleUrl: './sale-report.component.css'
})
export class SaleReportComponent implements OnInit, AfterViewInit {

  saleReportList: SaleRequestDTO[] = [];
  saleList: Sale[] = [];
  filteredSaleList: Sale[] = [];
  saleDetailList: DetailSaleRequestDTO[] = [];
  saleSelected: Sale | null = null;
  customerSale: string = '';
  notificationSale: string = '';
  statusTicket: boolean = true;
  detailSelected: DetailSaleRequestDTO | null = null;
  textFilter: string = '';
  startDate: string = '';
  finalDate: string = '';

  constructor(
    private saleService: SaleService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    private renderer: Renderer2,
    private excelService: ExcelService
  ) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.getSalesList();
  }

  /**
   * Obtiene una lista de ventas
   */
  getSalesList(): void {
    this.saleService.getAllSales().subscribe({
      next: (list) => {
        this.saleReportList = list;
        this.getDocumentAndCommentSale(this.saleReportList);
      },
      error: (error) => {
        console.log('Error al obtener la lista de ventas: ', error);
      }
    });
  }

  /**
   * Obtiene documento y comentario de una lista de ventas.
   * Crea un objeto Ventas con los datos de la lista.
   * Valida que el contenido del documento tenga una formato
   * @param list - Lista con los detalles de las ventas
   */
  getDocumentAndCommentSale(list: SaleRequestDTO[]): void {
    list.forEach(dto => {
      const documentMatch = dto.comment.match(/# (BO_\d+)/);
      const documentValue = documentMatch ? documentMatch[1] : null;

      const sale: Sale = {
        id: dto.id,
        date: dto.date,
        paymentMethod: dto.paymentMethod,
        document: documentValue ? documentValue : '',
        comment: dto.comment.replace(/# (BO_\d+)/, ''),
        discount: dto.discount,
        tax: dto.tax,
        totalSale: dto.total,
        status: dto.comment.includes('ANULADO') ? false : true,
        customer: null,
      };
      this.saleList.push(sale);
    });
    this.filteredSaleList = this.saleList;
  }

  /**
   * Obtiene los detalles de una venta seleccionado y actualiza sus propiedades
   * @param sale - Venta seleccionada
   */
  getSaleDetailsData(sale: Sale): void {
    this.saleSelected = sale;
    this.saleService.getDetailSale(this.saleSelected.id).subscribe({
      next: (list) => {
        this.saleDetailList = list;
        if (this.saleDetailList.length > 0) {
          this.customerSale = this.saleDetailList[0].customer;
          this.notificationSale = this.saleDetailList[0].notification;
          this.statusTicket = this.saleDetailList[0].ticketStatus;
        }
      },
      error: (error) => {
        console.log('Error al obtener detalles de la venta: ', error);
      }
    });
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
    this.filteredSaleList = this.saleList;
    this.tooltip.initializeTooltips();
  }

  /**
   * Descarga de datos de la lista (reportes) en un archivo excel
   * Transforma la lista filtrada a un formato y datos necesarios a mostrar
   */
  downloadExcel(): void{
    const transformedData = this.filteredSaleList.map(item => ({
      documento: item.document,
      fecha: item.date,
      MetodoPago: item.paymentMethod,
      iva: item.tax,
      total: item.totalSale,
      observacion: item.comment
    }));
    this.excelService.generateExcel(transformedData, 'Reporte-ventas');
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
