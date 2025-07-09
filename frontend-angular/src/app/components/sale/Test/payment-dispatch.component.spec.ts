import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentDispatchComponent } from '../payment-dispatch/payment-dispatch.component';

describe('PaymentDispatchComponent', () => {
  let component: PaymentDispatchComponent;
  let fixture: ComponentFixture<PaymentDispatchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentDispatchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentDispatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
