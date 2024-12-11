import { ComponentFixture, TestBed } from "@angular/core/testing";
import { CustomerComponent } from "./customer.component";
import { CustomerService } from "../../services/customer.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { of, throwError } from "rxjs";
import { PaymentStatus } from "../../models/enum/payment-status.enum";
import { Customer } from "../../models/Customer/customer.model";

describe('CustomerComponent', () => {
    let component: CustomerComponent;
    let fixture: ComponentFixture<CustomerComponent>;
    let customerService: jasmine.SpyObj<CustomerService>;
    let mockCustomer: Customer;
  
    beforeEach(async () => {
      const spy = jasmine.createSpyObj('CustomerService', ['getCustomer', 'updateCustomer']);
  
      await TestBed.configureTestingModule({
        imports: [FormsModule, CommonModule, CustomerComponent],
        providers: [
            provideHttpClientTesting(),
          { provide: CustomerService, useValue: spy }
        ]
      })
      .compileComponents();
  
      customerService = TestBed.inject(CustomerService) as jasmine.SpyObj<CustomerService>;
      mockCustomer =  mockCustomer = {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []};
    });
  
    beforeEach(() => {
      fixture = TestBed.createComponent(CustomerComponent);
      component = fixture.componentInstance;

      customerService.getCustomer.and.returnValue(of([]));
      customerService.updateCustomer.and.returnValue(of());
      fixture.detectChanges();
    });
  
    it('Debería crear el component de Cliente', () => {
      expect(component).toBeTruthy();
    });
  
    it('Debería traer clientes, "getAllCustomer(): void"', () => {
        const mockCustomers: Customer[] = [ mockCustomer,
            {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []}
        ];
  
      customerService.getCustomer.and.returnValue(of(mockCustomers));
  
      component.getAllCustomer();
  
      expect(component.customers.length).toBe(2);
      expect(component.customers).toEqual(mockCustomers);
      expect(component.filteredCustomers).toEqual(mockCustomers);
    });
  
    it('Debería manejar error cuando busque clientes', () => {
      customerService.getCustomer.and.returnValue(throwError(() => new Error('Error fetching customers')));
      spyOn(console, 'log');
  
      component.getAllCustomer();
  
      expect(console.log).toHaveBeenCalledWith('Error no se encontró ningún cliente', jasmine.any(Error));
    });
  
    it('Debería actualizar a un cliente, "updateCustomer(): void"', () => {
      component.selectedCustomer = mockCustomer;
  
      customerService.updateCustomer.and.returnValue(of(mockCustomer));
  
      component.updateCustomer();
  
      expect(component.selectedCustomer).toBeNull();
    });
  
    it('Debería manejar error cuando se actualiza un cliente', () => {
      component.selectedCustomer = mockCustomer;
  
      customerService.updateCustomer.and.returnValue(throwError(() => new Error('Error updating customer')));
      spyOn(console, 'log');
  
      component.updateCustomer();
  
      expect(console.log).toHaveBeenCalledWith('Error al actualizar cliente: ', jasmine.any(Error));
    });
  
    it('Debería actualizar el label de deuda total correctamente, "updateTotalDebtLabel(): void"', () => {
      component.customers = [ {...mockCustomer, debt: 1500, totalDebt: 1500},
        {id: 2, name: 'Ignacio', surname: 'Lopez', debt:1500, totalDebt: 1500, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []}
      ];
  
      component.updateTotalDebtLabel();
  
      expect(component.totalDebts).toBe(3000);
    });
  
    it('Debería filtrar clientes correctamente, "searchFilterCustomer(): void"', () => {
      component.customers = [ mockCustomer,
        {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: []}
      ];
  
      component.textFilter = 'juan';
      component.searchFilterCustomer();
  
      expect(component.filteredCustomers.length).toBe(1);
      expect(component.filteredCustomers[0].name).toBe('Juan');
    });

    it('Debería retornar el color correcto dependiendo el estado de la deuda, "statusColor(status: string): string"',() =>{
        expect(component.statusColor('PAGADA')).toBe('rgb(40, 238, 40)');
        expect(component.statusColor('PENDIENTE')).toBe('red');
        expect(component.statusColor('PARCIAL')).toBe('rgb(9, 180, 237)');
        expect(component.statusColor('VENCIDA')).toBe('blue');
        expect(component.statusColor('NULO')).toBe('transparent');
    });

    it('Debería retornar el estado correcto de la cuenta, "getStatusAccount(account: boolean): string"',() => {
        expect(component.getStatusAccount(true)).toBe('Activo');
        expect(component.getStatusAccount(false)).toBe('Inactivo');
    });

    it('Debería manejar el email correctamente, "getEmailEmpty(email: string): string"',() => {
        expect(component.getEmailEmpty('x@x.xxx')).toBe('Sin Registro');
        expect(component.getEmailEmpty('test@test.com')).toBe('test@test.com');
    });

    it('Debería editar correctamente a cliente seleccionado en el modal, "editModalCustomer(customer: Customer): void"',() => {
        spyOn(console,'error');
        const customerSelected = mockCustomer;
        component.editModalCustomer(customerSelected);
        expect(component.selectedCustomer).toEqual(customerSelected);

        component.editModalCustomer(undefined as any);
        expect(console.error).toHaveBeenCalledWith('No se pudo editar, el cliente es undefined');
    });
  });
  