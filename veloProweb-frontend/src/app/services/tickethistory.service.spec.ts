import { TestBed } from '@angular/core/testing';

import { TickethistoryService } from './tickethistory.service';

describe('TickethistoryService', () => {
  let service: TickethistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TickethistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
