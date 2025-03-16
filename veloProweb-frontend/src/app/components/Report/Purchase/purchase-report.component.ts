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
import { ExcelService } from '../../../utils/excel.service';

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
  sortDate: boolean = true;
  sortTotal: boolean = true;
  sortPosition: boolean = true;

  constructor(
    private purchaseService: PurchaseService,
    private supplierService: SupplierService,
    private notification: NotificationService,
    private tooltip: TooltipService,
    private renderer: Renderer2,
    private excelService: ExcelService) { }

  ngAfterViewInit(): void {
    this.renderer.listen('document', 'mouseover', () => {
      this.tooltip.initializeTooltips();
    });
  }

  ngOnInit(): void {
    this.getPurchasesList();
  }

  /**
   * Obtiene una lista de compras
   */
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

  /**
   * Asocia proveedores a la lista de compras recibida
   * Crea una compra con los datos y la agrega a una lista de compras.
   * @param list - Lista de compras
   */
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

  /**
   * Obtiene un proveedor asociado de una compra seleccionada
   * @param dto - Objeto contiene los datos de la compra
   * @returns - Proveedor o null si no encuentra registro 
   */
  async getSupplier(dto: PurchaseRequestDTO): Promise<Supplier | null> {
    try {
      const supplier = await firstValueFrom(this.supplierService.getSupplier(dto.idSupplier));
      return supplier || null;
    } catch (error) {
      console.error('Error al obtener proveedor:', error);
      return null;
    }
  }

  /**
   * Obtiene información de los detalles de compra de una compra seleccionada
   * @param purchase - Compra seleccionada
   */
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
      iva: item.tax,
      total: item.purchaseTotal,
      proveedor: item.supplier?.name,
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
        purchase.supplier?.name.toLowerCase().includes(this.textFilter.toLowerCase()));
    }
  }
}
