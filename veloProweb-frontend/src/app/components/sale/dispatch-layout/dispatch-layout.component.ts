import { Component, OnInit } from '@angular/core';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { DispatchService } from '../../../services/sale/dispatch.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DispatchPermissionsService } from '../../../services/permissions/dispatch-permissions.service';

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
  sortTrackingNumber: boolean = true;
  sortDeliveryDate: boolean = true;
  sortCreatedDate: boolean = true;

  constructor(
    private dispatchService: DispatchService,
    protected permission: DispatchPermissionsService){}

  ngOnInit(): void {
    this.loadDispatches();
  }

  loadDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
        this.filteredDispatchList = list;
      }
    });
  }

  toggleSortTrackingNumber(): void{
    this.sortTrackingNumber = !this.sortTrackingNumber;
    this.filteredDispatchList.sort((a, b) => {
      const trackingA = a.id;
      const trackingB = b.id;
      return this.sortTrackingNumber ? trackingA - trackingB : trackingB - trackingA;
    });
  }

  toggleSortCreatedDate(): void{
    this.sortCreatedDate = !this.sortCreatedDate;
    this.filteredDispatchList.sort((a, b) => {
      const dateA = new Date(a.created).getTime();
      const dateB = new Date(b.created).getTime();
      return this.sortCreatedDate ? dateA - dateB : dateB - dateA;
    });
  }
  
  toggleSortDeliveryDate(): void{
    this.sortDeliveryDate = !this.sortDeliveryDate;
    this.filteredDispatchList.sort((a, b) => {
      const dateA = new Date(a.deliveryDate).getTime();
      const dateB = new Date(b.deliveryDate).getTime();
      return this.sortDeliveryDate ? dateA - dateB : dateB - dateA;
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
