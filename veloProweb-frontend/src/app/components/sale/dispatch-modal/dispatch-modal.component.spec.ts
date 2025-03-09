import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DispatchModalComponent } from './dispatch-modal.component';

describe('DispatchModalComponent', () => {
  let component: DispatchModalComponent;
  let fixture: ComponentFixture<DispatchModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DispatchModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DispatchModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
