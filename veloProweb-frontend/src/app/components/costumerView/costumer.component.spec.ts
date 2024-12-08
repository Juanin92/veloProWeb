import { ComponentFixture, TestBed } from "@angular/core/testing";
import { CostumerComponent } from "./costumer.component";
import { CostumerService } from "../../services/costumer.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { of, throwError } from "rxjs";
import { PaymentStatus } from "../../models/enum/payment-status.enum";
import { Costumer } from "../../models/Costumer/costumer.model";

describe('CostumerComponent', () => {
    let component: CostumerComponent;
    let fixture: ComponentFixture<CostumerComponent>;
    let costumerService: jasmine.SpyObj<CostumerService>;
    let mockCustomer: Costumer;
  
    beforeEach(async () => {
      const spy = jasmine.createSpyObj('CostumerService', ['getCostumer', 'updateCostumer']);
  
      await TestBed.configureTestingModule({
        imports: [FormsModule, CommonModule, CostumerComponent],
        providers: [
            provideHttpClientTesting(),
          { provide: CostumerService, useValue: spy }
        ]
      })
      .compileComponents();
  
      costumerService = TestBed.inject(CostumerService) as jasmine.SpyObj<CostumerService>;
      mockCustomer =  mockCustomer = {id: 1, name: 'Juan', surname: 'Perez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []};
    });
  
    beforeEach(() => {
      fixture = TestBed.createComponent(CostumerComponent);
      component = fixture.componentInstance;

      costumerService.getCostumer.and.returnValue(of([]));
      costumerService.updateCostumer.and.returnValue(of());
      fixture.detectChanges();
    });
  
    it('Debería crear el component de Cliente', () => {
      expect(component).toBeTruthy();
    });
  
    it('Debería traer clientes, "getAllCostumer(): void"', () => {
        const mockCustomers: Costumer[] = [ mockCustomer,
            {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []}
        ];
  
      costumerService.getCostumer.and.returnValue(of(mockCustomers));
  
      component.getAllCostumer();
  
      expect(component.costumers.length).toBe(2);
      expect(component.costumers).toEqual(mockCustomers);
      expect(component.filteredCostumers).toEqual(mockCustomers);
    });
  
    it('Debería manejar error cuando busque clientes', () => {
      costumerService.getCostumer.and.returnValue(throwError(() => new Error('Error fetching customers')));
      spyOn(console, 'log');
  
      component.getAllCostumer();
  
      expect(console.log).toHaveBeenCalledWith('Error no se encontró ningún cliente', jasmine.any(Error));
    });
  
    it('Debería actualizar a un cliente, "updateCostumer(): void"', () => {
      component.selectedCostumer = mockCustomer;
  
      costumerService.updateCostumer.and.returnValue(of(mockCustomer));
  
      component.updateCostumer();
  
      expect(component.selectedCostumer).toBeNull();
    });
  
    it('Debería manejar error cuando se actualiza un cliente', () => {
      component.selectedCostumer = mockCustomer;
  
      costumerService.updateCostumer.and.returnValue(throwError(() => new Error('Error updating customer')));
      spyOn(console, 'log');
  
      component.updateCostumer();
  
      expect(console.log).toHaveBeenCalledWith('Error al actualizar cliente: ', jasmine.any(Error));
    });
  
    it('Debería actualizar el label de deuda total correctamente, "updateTotalDebtLabel(): void"', () => {
      component.costumers = [ {...mockCustomer, debt: 1500, totalDebt: 1500},
        {id: 2, name: 'Ignacio', surname: 'Lopez', debt:1500, totalDebt: 1500, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []}
      ];
  
      component.updateTotalDebtLabel();
  
      expect(component.totalDebts).toBe(3000);
    });
  
    it('Debería filtrar clientes correctamente, "searchFilterCostumer(): void"', () => {
      component.costumers = [ mockCustomer,
        {id: 2, name: 'Ignacio', surname: 'Lopez', debt:0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCostumerList: [], ticketHistoryList: []}
      ];
  
      component.textFilter = 'juan';
      component.searchFilterCostumer();
  
      expect(component.filteredCostumers.length).toBe(1);
      expect(component.filteredCostumers[0].name).toBe('Juan');
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

    it('Debería editar correctamente a cliente seleccionado en el modal, "editModalCostumer(costumer: Costumer): void"',() => {
        spyOn(console,'error');
        const costumerSelected = mockCustomer;
        component.editModalCostumer(costumerSelected);
        expect(component.selectedCostumer).toEqual(costumerSelected);

        component.editModalCostumer(undefined as any);
        expect(console.error).toHaveBeenCalledWith('No se pudo editar, el cliente es undefined');
    });
  });
  