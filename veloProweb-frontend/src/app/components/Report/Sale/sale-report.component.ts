import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { Sale } from '../../../models/entity/sale/sale';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaleService } from '../../../services/sale/sale.service';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { NotificationService } from '../../../utils/notification-service.service';
import { TooltipService } from '../../../utils/tooltip.service';
import { ExcelService } from '../../../utils/excel.service';
import { PdfService } from '../../../utils/pdf.service';
import { ReportPermissionsService } from '../../../services/permissions/report-permissions.service';

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
   * realiza una petición asíncrona para obtener los detalles de la venta
   * y los asigna a las propiedades relevantes.
   * @param sale - Venta seleccionada
   * @returns {Promise<void>} - Una promesa que se resuelve cuando se han obtenido y asignado
   * los datos detallados de la venta.
   */
  getSaleDetailsData(sale: Sale): Promise<void> {
    return new Promise((resolve, reject) => {
      this.saleSelected = sale;
     this.saleService.getDetailSale(this.saleSelected.id).subscribe({
      next: (list) => {
        this.saleDetailList = list;
        if (this.saleDetailList.length > 0) {
          this.customerSale = this.saleDetailList[0].customer;
          this.notificationSale = this.saleDetailList[0].notification;
          this.statusTicket = this.saleDetailList[0].ticketStatus;
          this.hasDispatchDetailsSale = this.saleDetailList[0].hasDispatch;

          if(this.hasDispatchDetailsSale){
            this.saleDetailList.push(this.createDispatchRegisterItem());
          }
        }
        resolve();
      },
      error: (error) => {
        console.log('Error al obtener detalles de la venta: ', error);
        reject(error);
      }
    });
    });
  }

  /**
   * Crea un objeto DetailSaleRequestDTO representa a un despacho
   * @returns - Devuelve el objeto
   */
  private createDispatchRegisterItem(): DetailSaleRequestDTO{
    return {
      descriptionProduct: 'Despacho',
      quantity: 1,
      price: 1000,
      tax: 0,
      customer: '',
      notification: '',
      ticketStatus: false,
      hasDispatch: true
    };
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
   * Imprimir una boleta de venta.
   * primero obtiene los datos detallados de la venta y luego genera un PDF
   * @param sale - Venta a imprimir
   * @returns {Promise<void>} - Una promesa que se resuelve cuando se ha generado el PDF.
   */
  async printSale(sale: Sale): Promise<void> {
    await this.getSaleDetailsData(sale);
  
    const saleDetails = {
      document: sale.document,
      customer: this.customerSale,
      date: sale.date,
      paymentMethod: sale.paymentMethod,
      totalSale: sale.totalSale,
      discount: sale.discount,
      tax: sale.tax,
      comment: sale.comment,
      items: this.saleDetailList.map(detail => ({
        quantity: detail.quantity,
        description: detail.descriptionProduct,
        price: detail.price
      }))
    };
    this.pdfService.generatePDF(saleDetails);
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
