import { AfterViewInit, Component, OnInit } from '@angular/core';
import { KardexServiceService } from '../../../services/Report/kardex-service.service';
import { Kardex } from '../../../models/Entity/kardex';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TooltipService } from '../../../utils/tooltip.service';
import { MovementsType } from '../../../models/enum/movements-type';

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

  constructor(
    private kardexService: KardexServiceService,
    private tooltip: TooltipService,
  ){}

  ngAfterViewInit(): void {
    this.tooltip.initializeTooltips();
  }

  ngOnInit(): void {
    this.getAllKardex();
  }

  getAllKardex(): void{
    this.kardexService.getAllKardex().subscribe({
      next:(list) =>{
        this.kardexList = list;
        this.filteredKardexList = list;
      },
      error:(error)=>{
        console.log('Registro no encontrado', error);
      }
    });
  }

  searchFilterRegisters(): void{
    if (this.textFilter.trim() === '') {
      this.filteredKardexList = this.kardexList;
    } else {
      this.filteredKardexList = this.kardexList.filter(kardex =>
        kardex.movementsType.toString().toLowerCase().includes(this.textFilter.toLowerCase()) || 
        kardex.product.description.toLowerCase().includes(this.textFilter.toLowerCase()));
    }
  }

  colorToMovementTypes(movementType: string): string{
    switch (movementType) {
      case 'AJUSTE': return 'text-bg-warning';
      case 'SALIDA': return 'text-bg-danger';
      case 'ENTRADA': return 'text-bg-success';
      default: return 'text-white';
    }
  }
}
