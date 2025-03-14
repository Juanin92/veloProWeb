import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DispatchLayoutComponent } from './dispatch-layout.component';

describe('DispatchLayoutComponent', () => {
  let component: DispatchLayoutComponent;
  let fixture: ComponentFixture<DispatchLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DispatchLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DispatchLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
