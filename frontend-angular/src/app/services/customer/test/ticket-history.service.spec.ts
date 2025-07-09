import { TestBed } from '@angular/core/testing';

import { TicketHistoryService } from '../ticket-history.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TicketHistory } from '../../../models/entity/customer/ticket-history';

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
      { id: 1, document: 'BO001', total: 2000 },
      { id: 2, document: 'BO002', total: 2000}
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
});
