// import { ComponentFixture, TestBed } from "@angular/core/testing";
// import { UpdateCustomerComponent } from "./update-customer.component";
// import { Customer } from "../../../models/Customer/customer.model";
// import { CustomerService } from "../../../services/customer.service";
// import { provideHttpClientTesting } from "@angular/common/http/testing";
// import { CommonModule } from "@angular/common";
// import { FormsModule } from "@angular/forms";
// import { of } from "rxjs/internal/observable/of";
// import { throwError } from "rxjs/internal/observable/throwError";
// import { PaymentStatus } from "../../../models/enum/payment-status.enum";

// describe('UpdateCustomerComponent', () => {
//   let component: UpdateCustomerComponent;
//   let fixture: ComponentFixture<UpdateCustomerComponent>;
//   let customerService: jasmine.SpyObj<CustomerService>;
//   let mockCustomer: Customer;

//   beforeEach(async () => {
//     const spy = jasmine.createSpyObj('CustomerService', ['updateCustomer']);

//     await TestBed.configureTestingModule({
//       imports: [FormsModule, CommonModule, UpdateCustomerComponent],
//       providers: [
//         provideHttpClientTesting(),
//         { provide: CustomerService, useValue: spy }
//       ]
//     })
//       .compileComponents();

//     customerService = TestBed.inject(CustomerService) as jasmine.SpyObj<CustomerService>;
//     mockCustomer = mockCustomer = { id: 1, name: 'Juan', surname: 'Perez', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, email: 'test@test.com', phone: '+569 12345678', paymentCustomerList: [], ticketHistoryList: [] };
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(UpdateCustomerComponent);
//     component = fixture.componentInstance;

//     // customerService.getCustomer.and.returnValue(of([]));
//     customerService.updateCustomer.and.returnValue(of());
//     fixture.detectChanges();
//   });

//   it('Debería crear el component de Actualizar Cliente', () => {
//     expect(component).toBeTruthy();
//   });

//   it('Debería actualizar a un cliente, "updateCustomer(): void"', () => {
//     component.selectedCustomer = mockCustomer;

//     customerService.updateCustomer.and.returnValue(of("Cliente actualizado correctamente!"));

//     component.updateCustomer();

//     expect(component.selectedCustomer).toEqual({
//       id: 0, name: '', surname: '', phone: '+569 ', email: '', debt: 0, totalDebt: 0, status: PaymentStatus.NULO, account: true, paymentCustomerList: [], ticketHistoryList: []
//     });
//   });

//   it('Debería manejar error cuando se actualiza un cliente', () => {
//     component.selectedCustomer = mockCustomer;
//     customerService.updateCustomer.and.returnValue(throwError(() => ({
//       error: {
//         error: 'Error updating customer'
//       }
//     })));
//     spyOn(console, 'log');

//     component.updateCustomer();

//     expect(console.log).toHaveBeenCalledWith('Error al actualizar cliente: ', 'Error updating customer');
//   });
// });