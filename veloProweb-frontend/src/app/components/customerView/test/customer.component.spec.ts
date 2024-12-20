import { ComponentFixture, TestBed } from "@angular/core/testing";
import { CustomerComponent } from "../customer.component";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { of, throwError } from "rxjs";
import { PaymentStatus } from "../../../models/enum/payment-status.enum";
import { Customer } from "../../../models/Entity/Customer/customer.model";
import { provideHttpClient } from "@angular/common/http";
import { CustomerService } from "../../../services/customer/customer.service";
import { NotificationService } from "../../../utils/notification-service.service";

describe('CustomerComponent', () => {
    let component: CustomerComponent;
    let fixture: ComponentFixture<CustomerComponent>;
    let customerService: jasmine.SpyObj<CustomerService>;
    let notification: jasmine.SpyObj<NotificationService>;
    let mockCustomer: Customer;

    beforeEach(async () => {
        const spy = jasmine.createSpyObj('CustomerService', ['getCustomer', 'deleteCustomer']);
        const notificationSpy = jasmine.createSpyObj('NotificationService', ['showConfirmation', 'showSuccessToast', 'showErrorToast']);

        await TestBed.configureTestingModule({
            imports: [CustomerComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                { provide: CustomerService, useValue: spy },
                { provide: NotificationService, useValue: notificationSpy },
            ]
        }).compileComponents();

        customerService = TestBed.inject(CustomerService) as jasmine.SpyObj<CustomerService>;
        notification = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
        mockCustomer = mockCustomer = { id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] };
    });

    beforeEach(() => {
        customerService.getCustomer.and.returnValue(of([mockCustomer]));
        fixture = TestBed.createComponent(CustomerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('Debería crear el component de Cliente', () => {
        expect(component).toBeTruthy();
    });

    //Prueba para traer lista de clientes
    it('Debería traer clientes, "getAllCustomer(): void"', () => {
        const mockCustomers: Customer[] = [
            mockCustomer,
            { id: 2, name: 'Ignacio', surname: 'Lopez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] }
        ];
        customerService.getCustomer.and.returnValue(of(mockCustomers));
        spyOn(component, 'updateTotalDebtLabel').and.callThrough();
        component.getAllCustomer();

        expect(component.customers.length).toBe(2);
        expect(component.customers).toEqual(mockCustomers);
        expect(component.filteredCustomers).toEqual(mockCustomers);
        expect(component.updateTotalDebtLabel).toHaveBeenCalled();
    });
    it('Debería manejar error cuando busque clientes', () => {
        customerService.getCustomer.and.returnValue(throwError(() => new Error('Error fetching customers')));
        spyOn(console, 'log');

        component.getAllCustomer();

        expect(console.log).toHaveBeenCalledWith('Error no se encontró ningún cliente', jasmine.any(Error));
    });

    //Prueba para eliminar un cliente
    it('Debería eliminar un cliente, "deleteCustomer(customer: Customer): void"', async () => {
        notification.showConfirmation.and.returnValue(Promise.resolve({ isConfirmed: true }));
        customerService.deleteCustomer.and.returnValue(of("Cliente eliminado exitosamente"));
        await component.deleteCustomer(mockCustomer);

        expect(notification.showConfirmation).toHaveBeenCalled();
        expect(customerService.deleteCustomer).toHaveBeenCalledWith(mockCustomer);
    });
    it('Debería manejar error al eliminar un cliente', async () => {
        const errorResponse = { error: { error: 'Error al eliminar cliente' } };
        notification.showConfirmation.and.returnValue(Promise.resolve({ isConfirmed: true }));
        customerService.deleteCustomer.and.returnValue(throwError(() => errorResponse));
        spyOn(console, 'log');
        await component.deleteCustomer(mockCustomer);

        expect(notification.showConfirmation).toHaveBeenCalled();
        expect(customerService.deleteCustomer).toHaveBeenCalledWith(mockCustomer);
        expect(console.log).toHaveBeenCalledWith('Error al eliminar cliente: ', errorResponse.error.error);
    });

    //Prueba para abrir modal con cliente seleccionado
    it('Debería abrir correctamente el modal con un cliente seleccionado, "openModalCustomer(customer: Customer): void"', () => {
        const customerSelected = mockCustomer;
        component.openModalCustomer(customerSelected);
        expect(component.selectedCustomer).toEqual(customerSelected);
        expect(mockCustomer).toEqual(customerSelected);
    });

    //Prueba para actualizar el valor de un label de deuda total
    it('Debería actualizar el label de deuda total correctamente, "updateTotalDebtLabel(): void"', () => {
        component.customers = [{ ...mockCustomer, debt: 1500, totalDebt: 1500 },
        { id: 2, name: 'Ignacio', surname: 'Lopez', debt: 1500, totalDebt: 1500, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] }
        ];
        component.updateTotalDebtLabel();

        expect(component.totalDebts).toBe(3000);
    });

    //Prueba para retornar un color a la celda dependiendo de la deuda
    it('Debería retornar el color correcto dependiendo el estado de la deuda, "statusColor(status: string): string"', () => {
        expect(component.statusColor('PAGADA')).toBe('rgb(40, 238, 40)');
        expect(component.statusColor('PENDIENTE')).toBe('red');
        expect(component.statusColor('PARCIAL')).toBe('rgb(9, 180, 237)');
        expect(component.statusColor('VENCIDA')).toBe('blue');
        expect(component.statusColor('NULO')).toBe('transparent');
    });

    //Prueba para tener el estado de una celda dependiendo de la cuenta 
    it('Debería retornar el estado correcto de la cuenta, "getStatusAccount(account: boolean): string"', () => {
        expect(component.getStatusAccount(true)).toBe('Activo');
        expect(component.getStatusAccount(false)).toBe('Inactivo');
    });

    //Prueba para manejar un formato a un email null
    it('Debería manejar el email correctamente, "getEmailEmpty(email: string): string"', () => {
        expect(component.getEmailEmpty('x@x.xxx')).toBe('Sin Registro');
        expect(component.getEmailEmpty('test@test.com')).toBe('test@test.com');
    });

    //Prueba para filtrar a cliente mediante una búsqueda
    it('Debería filtrar clientes correctamente, "searchFilterCustomer(): void"', () => {
        component.customers = [mockCustomer,
            { id: 2, name: 'Ignacio', surname: 'Lopez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] }
        ];

        component.textFilter = 'juan';
        component.searchFilterCustomer();

        expect(component.filteredCustomers.length).toBe(1);
        expect(component.filteredCustomers[0].name).toBe('Juan');
    });
});