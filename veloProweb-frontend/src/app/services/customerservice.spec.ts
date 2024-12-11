import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { CustomerService } from "./customer.service";
import { TestBed } from "@angular/core/testing";
import { PaymentStatus } from "../models/enum/payment-status.enum";
import { provideHttpClient, HttpClient } from "@angular/common/http";
import { Customer } from "../models/Customer/customer.model";

describe('CustomerService', () => {
    let service: CustomerService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
          providers: [
            CustomerService,
            provideHttpClient(),
            provideHttpClientTesting()
        ]
        });
        service = TestBed.inject(CustomerService);
        httpMock = TestBed.inject(HttpTestingController);
    });
    
    afterEach(() => {
        httpMock.verify();
    });  

    it('Debería ser creado el servicio de Customer', () => {
        expect(service).toBeTruthy();
    });

    it('Debería traer una lista de clientes, "getCustomer(): Observable<Customer[]>"', () => {
        const mockCustomers: Customer[] = [
            {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []},
            {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []}
        ];

        service.getCustomer().subscribe( customers => {
            expect(customers.length).toBe(2);
            expect(customers).toEqual(mockCustomers);
        });

        const req = httpMock.expectOne('http://localhost:8080/clientes');
        expect(req.request.method).toBe('GET');
        req.flush(mockCustomers);
    });

    it('Debería actualizar a un cliente, "updateCustomer(customer: Customer): Observable<Customer>"', () => {
        const mockCustomers: Customer = {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []};

        service.updateCustomer(mockCustomers).subscribe( customers => {
            expect(customers).toEqual(mockCustomers);
        });

        const req = httpMock.expectOne('http://localhost:8080/clientes/actualizar');
        expect(req.request.method).toBe('PUT');
        req.flush(mockCustomers);
    });
});