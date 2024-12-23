import { TestBed } from '@angular/core/testing';

import { ProductHelperService } from '../product-helper.service';

describe('ProductHelperService', () => {
  let service: ProductHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
