import { TestBed } from '@angular/core/testing';
import { KardexServiceService } from '../kardex-service.service';



describe('KardexServiceService', () => {
  let service: KardexServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KardexServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
