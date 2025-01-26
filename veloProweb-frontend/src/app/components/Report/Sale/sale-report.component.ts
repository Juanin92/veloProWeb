import { AfterViewChecked, AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { Sale } from '../../../models/Entity/Sale/sale';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaleService } from '../../../services/Sale/sale.service';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { NotificationService } from '../../../utils/notification-service.service';
import { TooltipService } from '../../../utils/tooltip.service';

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
    private renderer: Renderer2
  ) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.getSalesList();
  }

  getSalesList(): void {
    this.saleService.getAllSales().subscribe({
      next:(list) =>{
        this.saleReportList = list;
        this.getDocumentAndCommentSale(this.saleReportList);
      },
      error: (error) =>{
        console.log('Error al obtener la lista de ventas: ', error);
      }
    });
  }

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


  cleanInputFilterDates(): void {
    this.startDate = '';
    this.finalDate = '';
    this.filteredSaleList = this.saleList;
    this.tooltip.initializeTooltips();
  }

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
