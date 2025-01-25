import { Component, OnInit } from '@angular/core';
import { Sale } from '../../../models/Entity/Sale/sale';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SaleService } from '../../../services/Sale/sale.service';
import { SaleRequestDTO } from '../../../models/DTO/sale-request-dto';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';

@Component({
  selector: 'app-sale-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sale-report.component.html',
  styleUrl: './sale-report.component.css'
})
export class SaleReportComponent implements OnInit {

  saleReportList: SaleRequestDTO[] = [];
  saleList: Sale[] = [];
  saleDetailList: DetailSaleRequestDTO[] = [];
  saleSelected: Sale | null = null;
  customerSale: string = '';
  notificationSale: string = '';
  statusTicket: boolean = true;
  detailSelected: DetailSaleRequestDTO | null = null;

  constructor(
    private saleService: SaleService
  ) { }

  ngOnInit(): void {
    this.getSalesList();
  }

  getSalesList(): void {
    this.saleService.getAllSales().subscribe((List) => {
      this.saleReportList = List;
      this.getDocumentAndCommentSale(this.saleReportList);
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
  }

  getSaleDetailsData(sale: Sale): void {
    this.saleSelected = sale;
    this.saleService.getDetailSale(this.saleSelected.id).subscribe({
      next: (list) => {
        this.saleDetailList = list;
        if(this.saleDetailList.length > 0){
          console.log('fecha ',this.saleDetailList[0].notification);
          this.customerSale = this.saleDetailList[0].customer;
          this.notificationSale = this.saleDetailList[0].notification;
          this.statusTicket = this.saleDetailList[0].ticketStatus;
        }
      },
      error: (error) =>{
        console.log('Error al obtener detalles de la venta: ', error);
      } 
    });
  }

}
