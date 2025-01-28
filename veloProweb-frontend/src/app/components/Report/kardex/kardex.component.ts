import { AfterViewInit, Component, OnInit } from '@angular/core';
import { KardexServiceService } from '../../../services/Report/kardex-service.service';
import { Kardex } from '../../../models/Entity/kardex';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TooltipService } from '../../../utils/tooltip.service';

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
}
