import { TestBed } from '@angular/core/testing';

import { PaymentCustomerService } from '../payment-customer.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PaymentCustomerResponse } from '../../../models/Entity/Customer/payment-customer-response';
import { CustomerResponse } from '../../../models/Entity/Customer/customer-response';
import { TicketHistory } from '../../../models/Entity/Customer/ticket-history';

describe('PaymentCustomerService', () => {
  let service: PaymentCustomerService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PaymentCustomerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(PaymentCustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('Debería ser creado el servicio de PaymentCustomer', () => {
    expect(service).toBeTruthy();
  });

  //Pruebas API para obtener todos los pagos
  it('Debería traer una lista de pagos de clientes, "getAllPayments(): Observable<PaymentCustomer[]>"', () => {
    const mockPaymentCustomers: PaymentCustomerResponse[] = [
      { id: 1, amount: 1000, comment: 'Prueba', date: "2024-12-01", customer: {id: 1} as CustomerResponse, document: {id: 1} as TicketHistory},
      { id: 2, amount: 5000, comment: 'Prueba2', date: "2024-12-02", customer: {id: 2} as CustomerResponse, document: {id: 2} as TicketHistory}
    ];

    service.getAllPayments().subscribe(payments => {
      expect(payments.length).toBe(2);
      expect(payments).toEqual(mockPaymentCustomers);
    });

    const req = httpMock.expectOne('http://localhost:8080/pagos');
    expect(req.request.method).toBe('GET');
    req.flush(mockPaymentCustomers);
  });

  //Pruebas API para obtener todos los pagos de un cliente seleccionado
  it('Debería traer una lista de pagos de un clientes seleccionado, "getCustomerSelectedPayment(customerID: number): Observable<PaymentCustomer[]>"', () => {
    const mockPaymentCustomers: PaymentCustomerResponse[] = [
      { id: 1, amount: 1000, comment: 'Prueba', date: "2024-12-01", customer: {id: 1} as CustomerResponse, document: {id: 1} as TicketHistory},
      { id: 2, amount: 5000, comment: 'Prueba2', date: "2024-12-02", customer: {id: 2} as CustomerResponse, document: {id: 2} as TicketHistory}
    ];

    service.getCustomerSelectedPayment(1).subscribe(payments => {
      expect(payments.length).toBe(2);
      expect(payments).toEqual(mockPaymentCustomers);
    });

    const req = httpMock.expectOne(req => 
      req.url === 'http://localhost:8080/pagos/abonos' && req.params.get('customerId') === '1'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockPaymentCustomers);
  });
});
