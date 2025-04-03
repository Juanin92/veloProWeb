import { TestBed } from '@angular/core/testing';

import { TicketHistoryService } from '../ticket-history.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TicketHistory } from '../../../models/Entity/Customer/ticket-history.model';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { TicketRequestDTO } from '../../../models/DTO/ticket-request-dto';
import { PaymentStatus } from '../../../models/enum/payment-status.enum';

describe('TicketHistoryService', () => {
  let service: TicketHistoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
          TestBed.configureTestingModule({
            providers: [
              TicketHistoryService,
              provideHttpClient(),
              provideHttpClientTesting()
          ]
          });
          service = TestBed.inject(TicketHistoryService);
          httpMock = TestBed.inject(HttpTestingController);
      });
      
  afterEach(() => {
      httpMock.verify();
  });  
  

  it('Debería ser creado el servicio de Ticket', () => {
    expect(service).toBeTruthy();
  });

  //Prueba API para obtener los ticket del cliente
  it('Debería traer lista filtrada de ticket del cliente, "getListTicketByCustomer(customerId: number): Observable<TicketHistory[]>"', () => {
    const mockTickets: TicketHistory[] = [
      { id: 1, amount: 2000, document: 'BO001', total: 2000, status: false, date: "2024-05-10", notificationsDate: "2024-08-20", customer: { id: 1 } as Customer },
      { id: 2, amount: 2000, document: 'BO002', total: 2000, status: false, date: "2024-06-10", notificationsDate: "2024-08-20", customer: { id: 1 } as Customer }
    ];

    service.getListTicketByCustomer(1).subscribe(tickets => {
      expect(tickets.length).toBe(2);
      expect(tickets).toEqual(mockTickets);
    })

    const req = httpMock.expectOne(req =>
      req.url === 'http://localhost:8080/Tickets' && req.params.get('customerId') === '1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTickets);
  });

  //Prueba API para crear un ticket al cliente
  it('Debería crear ticket al cliente, "createTicketToCustomer(ticketRequestDTO: TicketRequestDTO): Observable<{message: string}>"', () => {
    const mockTicketDTO: TicketRequestDTO = {
      customer: { id: 1 } as Customer, number: 4, total: 2000, date: new Date()
    };
    const responseMessage = { message: "Ticket creado correctamente!" };
    service.createTicketToCustomer(mockTicketDTO).subscribe(response => {
      expect(response.message).toEqual("Ticket creado correctamente!");
    });
    const request = httpMock.expectOne('http://localhost:8080/Tickets');
    expect(request.request.method).toBe('POST');
    request.flush(responseMessage);
  });
  it('Debería manejar error al crear un nuevo ticket', () => {
    const mockTicketDTO: TicketRequestDTO = {
      customer: { id: 1 } as Customer, number: 4, total: 2000, date: new Date()
    };
    const errorMessage = { error: "Error al crear Ticket!" };
    service.createTicketToCustomer(mockTicketDTO).subscribe(
      response => fail('debería haber fallado'), error => {
        expect(error.status).toBe(400);
        expect(error.error.error).toEqual("Error al crear Ticket!");
      });
    const request = httpMock.expectOne('http://localhost:8080/Tickets');
    expect(request.request.method).toBe('POST');
    request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
  });

  //Prueba API para actualizar estado del ticket
  it('Debería actualizar estado del ticket, "updateStatus(ticket: TicketHistory): Observable<{message: string}>"', () => {
    const mockTicket: TicketHistory = {
      id: 1, amount: 2000, document: 'BO001', total: 2000, status: false, date: "2024-05-10", notificationsDate: "2024-08-20", customer: { id: 1 } as Customer
    };
     const responseMessage = { message: "Ticket actualizado correctamente!" };
     service.updateStatus(mockTicket).subscribe(response => {
       expect(response.message).toEqual("Ticket actualizado correctamente!");
     });
     const request = httpMock.expectOne('http://localhost:8080/Tickets/actualizar-estado');
     expect(request.request.method).toBe('PUT');
     request.flush(responseMessage);
   });
   it('Debería manejar error al validar ticket', () => {
     const mockTicket: TicketHistory = {
      id: 1, amount: 2000, document: 'BO001', total: 2000, status: false, date: "2024-05-10", notificationsDate: "2024-08-20", customer: { id: 1 } as Customer
     };
     const errorMessage = { error: "Error al actualizar Ticket!" };
     service.updateStatus(mockTicket).subscribe(
       response => fail('debería haber fallado'), error => {
         expect(error.status).toBe(400);
         expect(error.error.error).toEqual("Error al actualizar Ticket!");
       });
     const request = httpMock.expectOne('http://localhost:8080/Tickets/actualizar-estado');
     expect(request.request.method).toBe('PUT');
     request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
   });
});
