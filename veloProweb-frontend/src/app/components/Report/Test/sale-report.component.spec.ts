import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaleReportComponent } from '../Sale/sale-report.component';

describe('SaleReportComponent', () => {
  let component: SaleReportComponent;
  let fixture: ComponentFixture<SaleReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SaleReportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaleReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
