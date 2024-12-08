import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { CostumerService } from "./costumer.service";
import { TestBed } from "@angular/core/testing";
import { Costumer } from "../models/Costumer/costumer.model";
import { PaymentStatus } from "../models/enum/payment-status.enum";
import { provideHttpClient, HttpClient } from "@angular/common/http";

describe('CostumerService', () => {
    let service: CostumerService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
          providers: [
            CostumerService,
            provideHttpClient(),
            provideHttpClientTesting()
        ]
        });
        service = TestBed.inject(CostumerService);
        httpMock = TestBed.inject(HttpTestingController);
    });
    
    afterEach(() => {
        httpMock.verify();
    });  

    it('Debería ser creado el servicio de Costumer', () => {
        expect(service).toBeTruthy();
    });

    it('Debería traer una lista de clientes, "getCostumer(): Observable<Costumer[]>"', () => {
        const mockCustomers: Costumer[] = [
            {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []},
            {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []}
        ];

        service.getCostumer().subscribe( customers => {
            expect(customers.length).toBe(2);
            expect(customers).toEqual(mockCustomers);
        });

        const req = httpMock.expectOne('http://localhost:8080/clientes');
        expect(req.request.method).toBe('GET');
        req.flush(mockCustomers);
    });

    it('Debería actualizar a un cliente, "updateCostumer(costumer: Costumer): Observable<Costumer>"', () => {
        const mockCustomers: Costumer = {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []};

        service.updateCostumer(mockCustomers).subscribe( customers => {
            expect(customers).toEqual(mockCustomers);
        });

        const req = httpMock.expectOne('http://localhost:8080/clientes/actualizar');
        expect(req.request.method).toBe('PUT');
        req.flush(mockCustomers);
    });
});