import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { CustomerService } from "../customer.service";
import { TestBed } from "@angular/core/testing";
import { PaymentStatus } from "../../../models/enum/payment-status.enum";
import { provideHttpClient, HttpClient } from "@angular/common/http";
import { CustomerResponse } from "../../../models/Entity/Customer/customer-response";

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

    //Pruebas API para obtener todos los clientes
    it('Debería traer una lista de clientes, "getCustomer(): Observable<Customer[]>"', () => {
        const mockCustomers: CustomerResponse[] = [
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

    //Pruebas API para crear cliente
    it('Debería crear un nuevo cliente, addCustomer(customer: Customer): Observable<string>', () => {
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const responseMessage = {message: "Cliente agregado correctamente!"};
        service.addCustomer(mockCustomer).subscribe( response => {
            expect(response.message).toEqual("Cliente agregado correctamente!");
        });
        const request = httpMock.expectOne('http://localhost:8080/clientes/agregar');
        expect(request.request.method).toBe('POST');
        request.flush(responseMessage);
    });
    it('Debería manejar error al crear un nuevo cliente', () => {
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const errorMessage = { error: "Cliente Existente: Hay registro de este cliente." };
        
        service.addCustomer(mockCustomer).subscribe(
            response => fail('debería haber fallado'),
            error => {
                expect(error.status).toBe(400);
                expect(error.error.error).toEqual("Cliente Existente: Hay registro de este cliente.");
            }
        );
    
        const request = httpMock.expectOne('http://localhost:8080/clientes/agregar');
        expect(request.request.method).toBe('POST');
        request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
    
    //Pruebas API para actualizar cliente
    it('Debería actualizar a un cliente, "updateCustomer(customer: Customer): Observable<string>"', () => {
        const mockCustomers: CustomerResponse = {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []};
        const message = "Cliente actualizado correctamente!";

        service.updateCustomer(mockCustomers).subscribe( response => {
            expect(message).toEqual("Cliente actualizado correctamente!");
        });

        const req = httpMock.expectOne('http://localhost:8080/clientes/actualizar');
        expect(req.request.method).toBe('PUT');
        req.flush(message);
    });
    it('Debería manejar error al actualizar un cliente', () => {
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const errorMessage = { error: "Cliente Existente: Hay registro de este cliente." };
        
        service.updateCustomer(mockCustomer).subscribe(
            response => fail('debería haber fallado'),
            error => {
                expect(error.status).toBe(400);
                expect(error.error.error).toEqual("Cliente Existente: Hay registro de este cliente.");
            }
        );
    
        const request = httpMock.expectOne('http://localhost:8080/clientes/actualizar');
        expect(request.request.method).toBe('PUT');
        request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });

    //Pruebas API para eliminar un cliente
    it('Debería eliminar un cliente seleccionado, deleteCustomer(customer: Customer): Observable<string>', () =>{
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const responseMessage = "Cliente eliminado correctamente!";

        service.deleteCustomer(mockCustomer).subscribe(response =>{
                expect(response).toEqual("Cliente eliminado correctamente!");
            }
        );
        const request = httpMock.expectOne('http://localhost:8080/clientes/eliminar');
        expect(request.request.method).toBe('PUT');
        request.flush(responseMessage);
    });
    it('Debería manejar error al eliminar un cliente ya eliminado', () => {
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: false, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const errorMessage = "Cliente ya ha sido eliminado anteriormente.";
        
        service.deleteCustomer(mockCustomer).subscribe(
            response => fail('debería haber fallado'),
            error => {
                expect(error.status).toBe(400);
                expect(error.error).toEqual("Cliente ya ha sido eliminado anteriormente.");
            }
        );
    
        const request = httpMock.expectOne('http://localhost:8080/clientes/eliminar');
        expect(request.request.method).toBe('PUT');
        request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });

    //Pruebas API para activar cliente
    it('Debería activar un cliente eliminado, activeCustomer(customer: Customer): Observable<string>', () =>{
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: false, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const responseMessage = "Cliente ha sido activado";

        service.activeCustomer(mockCustomer).subscribe(response =>{
                expect(response).toEqual("Cliente ha sido activado");
            }
        );
        const request = httpMock.expectOne('http://localhost:8080/clientes/activar');
        expect(request.request.method).toBe('PUT');
        request.flush(responseMessage);
    });
    it('Debería manejar error al activar un cliente ya activado', () => {
        const mockCustomer: CustomerResponse = {
            id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: false, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []
        };
        const errorMessage = "El cliente tiene su cuenta activada";
        
        service.activeCustomer(mockCustomer).subscribe(
            response => fail('debería haber fallado'),
            error => {
                expect(error.status).toBe(400);
                expect(error.error).toEqual("El cliente tiene su cuenta activada");
            }
        );
    
        const request = httpMock.expectOne('http://localhost:8080/clientes/activar');
        expect(request.request.method).toBe('PUT');
        request.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
});