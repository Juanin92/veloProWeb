import { TestBed, ComponentFixture } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { PaymentCustomerService } from '../../../services/customer/payment-customer.service';
import { CustomerHelperServiceService } from '../../../services/customer/customer-helper-service.service';
import { PaymentStatus } from '../../../models/enum/payment-status.enum';
import { TicketHistory } from '../../../models/Entity/Customer/ticket-history.model';
import { Customer } from '../../../models/Entity/Customer/customer.model';
import { PaymentCustomerComponent } from '../payment-customer/payment-customer.component';
import { TicketHistoryService } from '../../../services/customer/ticket-history.service';
import { PaymentCustomer } from '../../../models/Entity/Customer/payment-customer.model';

describe('PaymentCustomerComponent', () => {
  let component: PaymentCustomerComponent;
  let fixture: ComponentFixture<PaymentCustomerComponent>;
  let paymentService: jasmine.SpyObj<PaymentCustomerService>;
  let customerHelper: jasmine.SpyObj<CustomerHelperServiceService>;
  let ticketService: jasmine.SpyObj<TicketHistoryService>;

  beforeEach(async () => {
    const paymentServiceSpy = jasmine.createSpyObj('PaymentCustomerService', ['getCustomerSelectedPayment']);
    const customerHelperSpy = jasmine.createSpyObj('CustomerHelperServiceService', ['createEmptyCustomer']);
    const ticketServiceSpy = jasmine.createSpyObj('TicketHistoryService', ['getListTicketByCustomer']);
    ticketServiceSpy.getListTicketByCustomer.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [PaymentCustomerComponent],
      providers: [
        { provide: PaymentCustomerService, useValue: paymentServiceSpy },
        { provide: CustomerHelperServiceService, useValue: customerHelperSpy },
        { provide: TicketHistoryService, useValue: ticketServiceSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentCustomerComponent);
    component = fixture.componentInstance;
    paymentService = TestBed.inject(PaymentCustomerService) as jasmine.SpyObj<PaymentCustomerService>;
    customerHelper = TestBed.inject(CustomerHelperServiceService) as jasmine.SpyObj<CustomerHelperServiceService>;
    ticketService = TestBed.inject(TicketHistoryService) as jasmine.SpyObj<TicketHistoryService>;

    customerHelper.createEmptyCustomer.and.returnValue({ id: 0, name: '', surname: '', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] });
    component.selectedCustomer = customerHelper.createEmptyCustomer();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  //Prueba para obtener los pagos realizados de un cliente
  it('debería obtener pagos y actualizar valores correctamente, "getPayments(customer: Customer): void"', () => {
    const mockPayments = [
      { id: 1, amount: 100, date: '2023-12-01', comment: 'prueba', document: {id: 1} as TicketHistory, customer: {id: 1} as Customer },
      { id: 2, amount: 200, date: '2023-12-02', comment: 'prueba', document: {id: 2} as TicketHistory, customer: {id: 1} as Customer },
    ];
    paymentService.getCustomerSelectedPayment.and.returnValue(of(mockPayments));
  
    component.getPayments({ id: 1} as Customer);
  
    expect(paymentService.getCustomerSelectedPayment).toHaveBeenCalledWith(1);
    expect(component.payments).toEqual(mockPayments);
    expect(component.paymentValue).toBe(300);
  });
  it('debería manejar errores al obtener pagos', () => {
    const errorResponse = { message: 'Error al obtener pagos' };
    paymentService.getCustomerSelectedPayment.and.returnValue(throwError(() => errorResponse));
  
    spyOn(console, 'log');
    component.getPayments({ id: 1} as Customer);
  
    expect(console.log).toHaveBeenCalledWith('Error no se encontró información de pagos ', errorResponse);
  });

  //Prueba para obtener una lista de tickets de un cliente
  it('Debería listar los tickets de un cliente, "getListTicketByCustomer(id: number): void"', () => {
    const mockCustomer = { id: 1, debt: 1000 } as Customer;
    component.selectedCustomer = mockCustomer;
    const mockTickets = [
      {id: 1, amount: 500, document: 'BO0020', total: 2000, status: true, date: '2025-01-01', notificationsDate: 'null', customer: mockCustomer},
      {id: 2, amount: 5000, document: 'BO0015', total: 20000, status: true, date: '2024-12-01', notificationsDate: 'null', customer: mockCustomer}
    ];
    ticketService.getListTicketByCustomer.and.returnValue(of(mockTickets));
    component.getListTicketByCustomer(mockCustomer.id);

    expect(ticketService.getListTicketByCustomer).toHaveBeenCalledWith(mockCustomer.id);
    expect(component.tickets).toEqual(mockTickets);
  });
  
  //Prueba que la suma de los pagos sea calculada
  it('debería actualizar el valor de los pagos, "updatePaymentValueLabel(): void"', () => {
    component.payments = [
        { id: 1, amount: 300, date: '2023-12-01', comment: 'prueba', document: {id: 1} as TicketHistory, customer: {id: 1} as Customer },
        { id: 2, amount: 200, date: '2023-12-02', comment: 'prueba', document: {id: 2} as TicketHistory, customer: {id: 1} as Customer },
    ];
  
    component.updatePaymentValueLabel();
  
    expect(component.paymentValue).toBe(500); 
  });

  //Prueba que se actualize label de la deuda del cliente
  it('debería actualizar el valor de la deuda, "updateDebtValueLabel(): void"', () => {
    const mockTicket = {id:1, total: 10000} as TicketHistory;
    const mockPayment = { document: { id: 1 }, amount: 200 } as PaymentCustomer;
    const mockEvent = { target: { checked: true } } as unknown as Event;

    component.payments = [mockPayment];
    component.selectedTicketsAmount = [];
    component.debtValue = 0;
  
    component.updateDebtValueLabel(mockTicket, mockEvent);
  
    expect(component.selectedTicketsAmount).toEqual([9800]);
    expect(component.debtValue).toBe(9800);
  });
  
  //Prueba de que se actualize label de la deuda total del cliente
  it('debería actualizar el valor total de la deuda, "updateTotalDebtLabel(): void "', () => {
    component.selectedCustomer = { id: 1, debt: 800 } as Customer;
  
    component.updateTotalDebtLabel();
  
    expect(component.totalDebt).toBe(800);
  });

  //Prueba cambios en las propiedades @Input() y verifica que el método getPayments se llame correctamente.
  it('debería llamar a getPayments al cambiar selectedCustomer', () => {
    spyOn(component, 'getPayments');
    const changes = {
      selectedCustomer: {
        currentValue: { id: 1, debt: 700, name: 'Juan', surname: 'Perez' } as Customer,
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true,
      },
    };
  
    component.ngOnChanges(changes);
  
    expect(component.getPayments).toHaveBeenCalledWith(changes.selectedCustomer.currentValue);
  });
  
  
});
