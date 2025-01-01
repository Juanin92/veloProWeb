import { Component, Input } from '@angular/core';
import { TicketHistoryService } from '../../../services/customer/ticket-history.service';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { TicketHistory } from '../../../models/Entity/Customer/ticket-history.model';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';

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
