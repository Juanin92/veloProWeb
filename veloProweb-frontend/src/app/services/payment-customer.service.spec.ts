import { TestBed } from '@angular/core/testing';

import { PaymentCustomerService } from './payment-customer.service';

describe('PaymentCustomerService', () => {
  let service: PaymentCustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentCustomerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
