import { AfterViewInit, Component, OnInit } from '@angular/core';
import { KardexServiceService } from '../../../services/inventory/kardex-service.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TooltipService } from '../../../utils/tooltip.service';
import { ExcelService } from '../../../utils/excel.service';
import { ReportPermissionsService } from '../../../services/permissions/report-permissions.service';
import { Kardex } from '../../../models/entity/inventory/kardex';

@Component({
  selector: 'app-kardex',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './kardex.component.html',
  styleUrl: './kardex.component.css'
})
export class KardexComponent implements OnInit, AfterViewInit{

  kardexList: Kardex[] = [];
  filteredKardexList: Kardex[] = [];
  textFilter: string = '';
  sortDate: boolean = true;
  sortPosition: boolean = true;

  constructor(
    private kardexService: KardexServiceService,
    private tooltip: TooltipService,
    private excelService: ExcelService,
    protected permission: ReportPermissionsService
  ){}

  ngAfterViewInit(): void {
    this.tooltip.initializeTooltips();
  }

  ngOnInit(): void {
    this.loadKardexList();
  }

  /**
   * Obtiene una lista de reportes de productos
   */
  loadKardexList(): void{
    this.kardexService.getAllKardex().subscribe({
      next:(list) =>{
        this.kardexList = list;
        this.filteredKardexList = list;
      }
    });
  }

  /**
   * Descarga de datos de la lista (reportes) en un archivo excel
   * Transforma la lista filtrada a un formato y datos necesarios a mostrar
   */
  exportKardexToExcel(): void{
    const transformedData = this.filteredKardexList.map(item => ({
      fecha: item.date,
      descripción: item.product,
      stock: item.stock,
      precio: item.price,
      movimiento: item.movementsType,
      cantidad: item.quantity,
      usuario: item.user,
      observación: item.comment
    }));
    this.excelService.generateExcel(transformedData, 'Registro-Productos');
  }

  toggleSortDate(): void{
    this.sortDate = !this.sortDate;
    this.filteredKardexList.sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return this.sortDate ? dateA - dateB : dateB - dateA;
    });
  }

  toggleSortPosition(): void{
    this.filteredKardexList.reverse();
    this.sortPosition = !this.sortPosition;
  }

  /**
   *Filtra las registro según el texto ingresado en el campo de búsqueda.
   *Busca coincidencias en el tipos de movimientos y descripción de los productos.
   */
  searchFilterRegisters(): void{
    if (this.textFilter.trim() === '') {
      this.filteredKardexList = this.kardexList;
    } else {
      this.filteredKardexList = this.kardexList.filter(kardex =>
        kardex.movementsType.toString().toLowerCase().includes(this.textFilter.toLowerCase()) || 
        kardex.product.toLowerCase().includes(this.textFilter.toLowerCase()));
    }
  }

  /**
   * Asigna una clase de color a un tipo de movimiento.
   * @param movementType - Tipo de movimiento
   * @returns - Clase bootstrap correspondiente al color del movimiento
   */
  colorToMovementTypes(movementType: string): string{
    switch (movementType) {
      case 'AJUSTE': return 'text-bg-warning';
      case 'SALIDA': return 'text-bg-danger';
      case 'ENTRADA': return 'text-bg-success';
      default: return 'text-white';
    }
  }
}
