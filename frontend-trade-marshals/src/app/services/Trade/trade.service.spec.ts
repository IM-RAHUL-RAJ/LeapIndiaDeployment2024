import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { TradeService } from './trade.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DatePipe } from '@angular/common';

describe('TradeService', () => {
  let service: TradeService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        DatePipe,
      ]
    });
    service = TestBed.inject(TradeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // it('should POST to add a book', inject([TradeService],
  //   fakeAsync((service: TradeService) => {
  //   const expected = new Order('', 1, 1, '', '', '', 1);
  //   service.orderRequest(expected)
  //   .subscribe();
  //   const req = httpTestingController.expectOne(
  //   'http://localhost:3000/fmts/trades/trade');
  //   // Assert that the request is a POST.
  //   expect(req.request.method).toEqual('POST');
  //   // Assert that it was called with the right data
  //   expect(req.request.body).toBe(undefined);
  // })));

});
