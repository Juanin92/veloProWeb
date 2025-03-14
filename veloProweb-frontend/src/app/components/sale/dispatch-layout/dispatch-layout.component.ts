import { Component, OnInit } from '@angular/core';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dispatch-layout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch-layout.component.html',
  styleUrl: './dispatch-layout.component.css'
})
export class DispatchLayoutComponent implements OnInit{

  dispatchList: Dispatch[] = [];
  filteredDispatchList: Dispatch[] = [];
  textFilter: string = '';

  constructor(private dispatchService: DispatchService){}

  ngOnInit(): void {
    this.getDispatches();
  }

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
        this.filteredDispatchList = list;
      },
      error: (error)=>{
        console.log('No se encontró información sobre los despachos registrados');
      }
    });
  }

  searchFilterDispatches(): void {
    if (this.textFilter.trim() === '') {
      this.filteredDispatchList = this.dispatchList;
    } else {
      this.filteredDispatchList = this.dispatchList.filter(dispatch =>
        dispatch.customer.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        dispatch.status.toLowerCase().includes(this.textFilter.toLowerCase()) || 
        dispatch.address.toLowerCase().includes(this.textFilter.toLowerCase())
      );
    }
  }
}
