import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { PaymentCustomerService } from '../../../services/customer/payment-customer.service';
import { CustomerResponse } from '../../../models/entity/customer/customer-response';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { PaymentCustomerResponse } from '../../../models/entity/customer/payment-customer-response';
import { CommonModule } from '@angular/common';
import { TicketHistoryService } from '../../../services/customer/ticket-history.service';
import { TicketHistory } from '../../../models/entity/customer/ticket-history';
import { PaymentDetails } from '../../../models/entity/customer/payment-details';
import { FormsModule } from '@angular/forms';
import { PaymentValidator } from '../../../validation/payment-validator';
import { NotificationService } from '../../../utils/notification-service.service';
import { ModalService } from '../../../utils/modal.service';
import { CustomerPermissionsService } from '../../../services/permissions/customer-permissions.service';
import { ErrorMessageService } from '../../../utils/error-message.service';

@Component({
  selector: 'app-payment-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-customer.component.html',
  styleUrl: './payment-customer.component.css'
})
export class PaymentCustomerComponent implements OnChanges {

  @Input() selectedCustomer: CustomerResponse; //Cliente seleccionado desde un componente padre
  @Output() paymentRealized = new EventEmitter<void>();
  payments: PaymentCustomerResponse[] = []; //Lista de pagos 
  tickets: TicketHistory[] = []; //Lista de tickets
  paymentRequest: PaymentDetails;
  totalDebt: number = 0;
  debtValue: number = 0;
  paymentValue: number = 0;
  selectedTickets: TicketHistory[] = [];
  validation = PaymentValidator;


  constructor(
    private paymentService: PaymentCustomerService,
    private customerHelper: CustomerHelperServiceService,
    private ticketService: TicketHistoryService,
    protected permission: CustomerPermissionsService,
    private notification: NotificationService,
    public modalService: ModalService,
    private errorMessage: ErrorMessageService) {
    this.selectedCustomer = customerHelper.createEmptyCustomer();
    this.paymentRequest = this.resetPayments();
  }

  /**
   * Método para ejecutar una acción cuando se hace cambio en una propiedad
   * reinicia la variable debtValue y la lista una vacía
   * @param changes -  Objeto que contiene los cambios detectados en las propiedades de entrada.
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedCustomer'] && changes['selectedCustomer'].currentValue) {
      this.debtValue = 0;
      this.selectedTickets = [];
      //Llama al método para obtener los pagos del cliente con valor actualizados
      this.getPayments(changes['selectedCustomer'].currentValue);
    }
  }

  /**
   * Realiza el pago de tickets del cliente
   * Valida que los datos del objetos sean correctos
   */
  createPaymentCustomer(): void {
    this.paymentRequest.ticketIDs = this.selectedTickets.map(ticket => ticket.id);
    this.paymentRequest.customerID = this.selectedCustomer.id;
    this.paymentRequest.totalPaymentPaid = this.paymentValue;
    if (this.validation.validateFormPayment(this.paymentRequest)) {
      this.paymentService.createPaymentCustomer(this.paymentRequest).subscribe({
        next: (response) => {
          this.notification.showSuccessToast("Pago realizado", 'center', 3000);
          this.getPayments(this.selectedCustomer);
          this.getListTicketByCustomer(this.selectedCustomer.id);
          this.paymentRequest = this.resetPayments();
          this.paymentRealized.emit();
          this.modalService.closeModal();
        }, error: (error) => {
          const message = this.errorMessage.errorMessageExtractor(error);
          console.log('Problema al realizar pago: \t', message);
          this.notification.showErrorToast(`Error al realizar el pago \n${message}`, 'top', 5000);
        }
      });
    } else {
      this.notification.showWarning("Advertencia", "Falta datos para realizar el pago");
    }
  }

  /**
   * Obtiene la lista de pagos asociados a un cliente.
   * Actualiza los monto de deuda a pagar, deuda total y abonos
   * llama el método para obtener los ticket del cliente 
   * @param customer - Cliente para el cual se recuperan los pagos.
   */
  getPayments(customer: CustomerResponse): void {
    this.paymentService.getCustomerSelectedPayment(customer.id).subscribe({
      next: (paymentList) => {
        this.payments = paymentList;
        this.updatePaymentValueLabel();
        this.updateTotalDebtLabel();
        this.getListTicketByCustomer(customer.id);
      }, error: (error) => {
        const message = this.errorMessage.errorMessageExtractor(error);
        console.log('Error no se encontró información de pagos ', message);
      }
    });
  }

  /**
   * Obtiene la lista de tickets asociados al cliente seleccionado.
   * @param id - Identificador del cliente.
   */
  getListTicketByCustomer(id: number): void {
    this.ticketService.getListTicketByCustomer(this.selectedCustomer.id).subscribe({
      next: (ticketList) => {
        this.tickets = ticketList;
        console.log('ID cliente seleccionado; ', id);
        console.log('lista ticket: ',  ticketList);
      },error: (error) => {
        const message = this.errorMessage.errorMessageExtractor(error);
        console.log('Error no se encontró información de los tickets ', message);
      }
    });
  }

  /**
   * Actualiza el valor acumulado de deuda en base a los tickets seleccionados.
   * Valida si ticket fue marcado o no con un check
   * Calcula el valor acumulado de las deudas seleccionadas sumando todos los monto en la lista.
   * Busca un pago en la lista de pagos si este coincide con ID ticket seleccionado.
   * Calcula el monto, Si hay un pago asociado, resta el monto pagado del total del ticket; de lo contrario, usa el total completo.
   * @param ticket - Ticket seleccionado o no seleccionado.
   * @param event - Evento asociado a la acción de check del ticket.
   */
  updateDebtValueLabel(ticket: TicketHistory, event: Event): void {
    const checkbox = event.target as HTMLInputElement; //Obtiene el estado del checkbox desde el evento del DOM.
    if (checkbox.checked) {
      this.selectedTickets.push(ticket);
    } else {
      const index = this.selectedTickets.findIndex(t => t.id === ticket.id);
      if (index > -1) {
        this.selectedTickets.splice(index, 1);
      }
    }
    this.debtValue = Array.from(this.selectedTickets).reduce((collector, current) => {
      const payment = this.payments.find(payment => payment.document === current.document);
      const ticketAmount = payment ? current.total - payment.amount : current.total;
      return collector + ticketAmount;
    }, 0);
    console.log('debtValue: ', this.debtValue);
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

  /**
   * Inicializa los valores del objeto DTO
   * @returns - DTO con sus valores predeterminados inicializados
   */
  resetPayments(): PaymentDetails {
    return {
      ticketIDs: [],
      customerID: 0,
      amount: 0,
      comment: '',
      totalPaymentPaid: 0
    }
  }
}
