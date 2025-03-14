import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CashierMovementsComponent } from './cashier-movements.component';

describe('CashierMovementsComponent', () => {
  let component: CashierMovementsComponent;
  let fixture: ComponentFixture<CashierMovementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CashierMovementsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CashierMovementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
