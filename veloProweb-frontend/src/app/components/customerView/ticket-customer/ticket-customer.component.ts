import { Component, Input } from '@angular/core';
import { TicketHistoryService } from '../../../services/ticket-history.service';
import { Customer } from '../../../models/Customer/customer.model';
import { TicketHistory } from '../../../models/Customer/ticket-history.model';
import { CustomerHelperServiceService } from '../../../services/customer-helper-service.service';

@Component({
  selector: 'app-ticket-customer',
  standalone: true,
  imports: [],
  templateUrl: './ticket-customer.component.html',
  styleUrl: './ticket-customer.component.css'
})
export class TicketCustomerComponent {

  @Input() selectedCustomer: Customer;
  tickets: TicketHistory[] = []; 

  constructor(
    private ticketService: TicketHistoryService,
    private customerHelper: CustomerHelperServiceService){
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  
}
