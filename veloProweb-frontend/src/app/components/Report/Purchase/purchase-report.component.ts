import { AfterViewInit, Component, OnInit, Renderer2 } from '@angular/core';
import { PurchaseService } from '../../../services/Purchase/purchase.service';
import { PurchaseRequestDTO } from '../../../models/DTO/purchase-request-dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Purchase } from '../../../models/Entity/Purchase/purchase';
import { TooltipService } from '../../../utils/tooltip.service';
import { Supplier } from '../../../models/Entity/Purchase/supplier';
import { SupplierService } from '../../../services/Purchase/supplier.service';
import { NotificationService } from '../../../utils/notification-service.service';
import { firstValueFrom } from 'rxjs';
import { DetailPurchaseRequestDTO } from '../../../models/DTO/detail-purchase-request-dto';

@Component({
  selector: 'app-purchase-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-report.component.html',
  styleUrl: './purchase-report.component.css'
})
export class PurchaseReportComponent implements OnInit, AfterViewInit {

  purchaseList: Purchase[] = [];
  filteredPurchaseList: Purchase[] = [];
  purchaseSelected: Purchase | null = null;
  purchaseDetailList: DetailPurchaseRequestDTO[] = [];
  textFilter: string = '';
  startDate: string = '';
  finalDate: string = '';

  constructor(
    private purchaseService: PurchaseService,
    private supplierService: SupplierService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    private renderer: Renderer2) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.getPurchasesList();
  }

  getPurchasesList(): void {
    this.purchaseService.getAllPurchases().subscribe({
      next: (list) => {
        this.getSupplierPurchase(list);
      },
      error: (error) => {
        console.log('No se encontró ninguna compra');
      }
    });
  }

  async getSupplierPurchase(list: PurchaseRequestDTO[]): Promise<void> {
    for (const dto of list) {
      const supplier = await this.getSupplier(dto);
      const purchase: Purchase = {
        id: dto.id,
        date: dto.date,
        document: dto.document,
        documentType: dto.documentType,
        tax: dto.tax,
        purchaseTotal: dto.total,
        supplier: supplier,
      };
      this.purchaseList.push(purchase);
    }
    this.filteredPurchaseList = this.purchaseList;
  }

  async getSupplier(dto: PurchaseRequestDTO): Promise<Supplier | null> {
    try {
      const supplier = await firstValueFrom(this.supplierService.getSupplier(dto.idSupplier));
      return supplier || null;
    } catch (error) {
      console.error('Error al obtener proveedor:', error);
      return null;
    }
  }

  getPurchaseDetailsData(purchase: Purchase): void {
    this.purchaseSelected = purchase;
    this.purchaseService.getDetailPurchase(this.purchaseSelected.id).subscribe({
      next: (list) => {
        this.purchaseDetailList = list;
      },
      error: (error) => {
        console.log('Error al obtener detalles de la compra: ', error);
      }
    });
  }

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


  cleanInputFilterDates(): void {
    this.startDate = '';
    this.finalDate = '';
    this.filteredPurchaseList = this.purchaseList;
  }

  searchFilterPurchases(): void {
    if (this.textFilter.trim() === '') {
      this.filteredPurchaseList = this.purchaseList;
    } else {
      this.filteredPurchaseList = this.purchaseList.filter(purchase =>
        purchase.document.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        purchase.documentType.toLowerCase().includes(this.textFilter.toLowerCase()) ||
        purchase.supplier?.name.toLowerCase().includes(this.textFilter.toLowerCase()));
    }
  }
}
