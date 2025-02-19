import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DispatchService } from '../../../services/Sale/dispatch.service';
import { Dispatch } from '../../../models/Entity/Sale/dispatch';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DetailSaleRequestDTO } from '../../../models/DTO/detail-sale-request-dto';
import { SaleDetail } from '../../../models/Entity/Sale/sale-detail';
import { SaleDetailDTO } from '../../../models/DTO/sale-detail-dto';

@Component({
  selector: 'app-dispatch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css'
})
export class DispatchComponent implements OnInit{

  @Input() saleDetailList: SaleDetail[] = [];
  @Output() dispatchUpdated = new EventEmitter<Dispatch[]>();
  dispatchList: Dispatch[] = [];
  saleDetailDTOList: SaleDetailDTO[] = [];
  dispatch: Dispatch = {
    id: 0,
    trackingNumber: '',
    status: '',
    address: '',
    comment: '',
    customer: '',
    created: '',
    deliveryDate: '',
    saleDetail: null,
  }

  constructor(private dispatchService: DispatchService){}

  ngOnInit(): void {
    this.getDispatches();
  }

  getDispatches(): void{
    this.dispatchService.getDispatches().subscribe({
      next:(list)=>{
        this.dispatchList = list;
      }, error: (error)=>{
        console.log('Error al obtener despachos', error.error?.error);
      }
    });
  }

  createNewDispatch(dispatch: Dispatch): void{
    this.saleDetailDTOList = this.saleDetailList.map(saleDetail => {
      return {
        id: saleDetail.id,
        idProduct: saleDetail.product.id,
        quantity: saleDetail.quantity
      };
    });
    dispatch.saleDetail = this.saleDetailDTOList;
    // this.dispatchService.createDispatch(this.dispatch).subscribe();
  }

  resetModal(): void{
    this.dispatch.address = '';
    this.dispatch.comment = '';
    this.dispatch.customer = '';
  }
}
