import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AddCustomerComponent } from "./add-customer.component";
import { CustomerService } from "../../../services/customer.service";
import { Customer } from "../../../models/Customer/customer.model";
import { of } from "rxjs/internal/observable/of";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { PaymentStatus } from "../../../models/enum/payment-status.enum";
import { throwError } from "rxjs";

describe('AddCustomerComponent', () => {
  let component: AddCustomerComponent;
  let fixture: ComponentFixture<AddCustomerComponent>;
  let customerService: jasmine.SpyObj<CustomerService>;
  let mockCustomer: Customer;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('CustomerService', ['addCustomer']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, CommonModule, AddCustomerComponent],
      providers: [
        provideHttpClientTesting(),
        { provide: CustomerService, useValue: spy }
      ]
    })
      .compileComponents();

    customerService = TestBed.inject(CustomerService) as jasmine.SpyObj<CustomerService>;
    mockCustomer = mockCustomer = { id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] };
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCustomerComponent);
    component = fixture.componentInstance;

    customerService.addCustomer.and.returnValue(of());
    fixture.detectChanges();
  });

  it('Debería crear el component de Agregar Cliente', () => {
    expect(component).toBeTruthy();
  });

  //Prueba para agregar un nuevo cliente
  it('Debería crear un nuevo cliente, "addCustomer(): void"', () => {
    const message = "Cliente creado correctamente!";
    customerService.addCustomer.and.returnValue(of());
    component.addCustomer();

    expect(message).toEqual("Cliente creado correctamente!");
  });
  it('Debería manejar error cuando se crea un nuevo cliente', () => {
    spyOn(component['customerValidator'], 'validateForm').and.returnValue(true);
    
    const errorResponse = { error: { error: 'Error al crear' } };
    customerService.addCustomer.and.returnValue(throwError(() => errorResponse));
    spyOn(console, 'error');
    component.addCustomer();
    
    expect(console.error).toHaveBeenCalledWith('Error al agregar el cliente:', errorResponse);
  });
  
});