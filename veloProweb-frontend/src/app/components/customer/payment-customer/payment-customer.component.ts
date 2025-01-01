import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { PaymentCustomerService } from '../../../services/customer/payment-customer.service';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { PaymentCustomer } from '../../../models/Entity/Customer/payment-customer.model';
import { CommonModule } from '@angular/common';
import { TicketHistoryService } from '../../../services/customer/ticket-history.service';
import { TicketHistory } from '../../../models/Entity/Customer/ticket-history.model';
import { tick } from '@angular/core/testing';

@Component({
  selector: 'app-payment-customer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-customer.component.html',
  styleUrl: './payment-customer.component.css'
})
export class PaymentCustomerComponent implements OnChanges {

  @Input() selectedCustomer: Customer; //Cliente seleccionado desde un componente padre
  payments: PaymentCustomer[] = []; //Lista de pagos 
  tickets: TicketHistory[] = []; //Lista de tickets
  totalDebt: number = 0; 
  debtValue: number = 0; 
  paymentValue: number = 0;
  selectedTicketsAmount: number[] = []; //Lista de monto deuda de tickets seleccionados
  

  constructor(
    private paymentService: PaymentCustomerService,
    private customerHelper: CustomerHelperServiceService,
    private ticketService: TicketHistoryService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
  }

  /**
   * Método para ejecutar una acción cuando se hace cambio en una propiedad
   * reinicia la variable debtValue y la lista una vacía
   * @param changes -  Objeto que contiene los cambios detectados en las propiedades de entrada.
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedCustomer'] && changes['selectedCustomer'].currentValue) { 
      this.debtValue = 0;
      this.selectedTicketsAmount = [];
      //Llama al método para obtener los pagos del cliente con valor actualizados
      this.getPayments(changes['selectedCustomer'].currentValue); 
    }
  }

  /**
   * Obtiene la lista de pagos asociados a un cliente.
   * Actualiza los monto de deuda a pagar, deuda total y abonos
   * llama el método para obtener los ticket del cliente 
   * @param customer - Cliente para el cual se recuperan los pagos.
   */
  getPayments(customer: Customer): void {
    this.paymentService.getCustomerSelectedPayment(customer.id).subscribe((paymentList) => {
      this.payments = paymentList;
      this.updatePaymentValueLabel();
      this.updateTotalDebtLabel();
      this.getListTicketByCustomer(customer.id);
    }, (error) => {
      console.log('Error no se encontró información de pagos ', error);
    });
  }

  /**
   * Obtiene la lista de tickets asociados al cliente seleccionado.
   * @param id - Identificador del cliente.
   */
  getListTicketByCustomer(id: number): void{
    this.ticketService.getListTicketByCustomer(this.selectedCustomer.id).subscribe((ticketList) => {
      this.tickets = ticketList;
    }, (error) => {
      console.log('Error no se encontró información de los tickets ', error);
    });
  } 

  /**
   * Actualiza el valor acumulado de deuda en base a los tickets seleccionados.
   * Busca un pago en la lista de pagos si este coincide con ID ticket seleccionado.
   * Calcula el monto, Si hay un pago asociado, resta el monto pagado del total del ticket; de lo contrario, usa el total completo.
   * Valida si ticket fue marcado o no con un check
   * Calcula el valor acumulado de las deudas seleccionadas sumando todos los monto en la lista.
   * @param ticket - Ticket seleccionado o no seleccionado.
   * @param event - Evento asociado a la acción de check del ticket.
   */
  updateDebtValueLabel(ticket: TicketHistory, event: Event): void {
    const checkbox = event.target as HTMLInputElement; //Obtiene el estado del checkbox desde el evento del DOM.
    const payment = this.payments.find(payment => payment.document.id === ticket.id);
    const ticketAmount =  payment ? ticket.total - payment.amount : ticket.total;
    if (checkbox.checked) {
      this.selectedTicketsAmount.push(ticketAmount);
    }else{
      const index = this.selectedTicketsAmount.indexOf(ticketAmount);
      if (index > -1) {
          this.selectedTicketsAmount.splice(index, 1);
      }
    }
    this.debtValue = Array.from(this.selectedTicketsAmount).reduce((collector, current) => collector + current, 0);
  }

  /**
   * Actualiza el valor total de los pagos abonados por el cliente seleccionado.
   */
  updatePaymentValueLabel(): void {
    this.paymentValue = this.payments.reduce((sum, payment) => sum + payment.amount, 0);
  }

  /**
   * Actualiza el total de la deuda del cliente seleccionado.
   */
  updateTotalDebtLabel(): void {
    this.totalDebt = this.selectedCustomer.debt;
  }
}
