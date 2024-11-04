import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { PriceService } from './price.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';

describe('PriceService', () => {
  let service: PriceService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(PriceService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should handle a 404 error', inject([PriceService],
    fakeAsync((service: PriceService) => {
      let errorResp: HttpErrorResponse;
      let errorReply: string = '';
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.getPricesFromFMTS()
        .subscribe({
          next: () => fail('Should not succeed'),
          error: (err) => errorReply = err
        });
      const req = httpTestingController.expectOne(service.url);
      // Assert that the request is a GET.
      expect(req.request.method).toEqual('GET');
      // Respond with error
      req.flush('Forced 404', {
        status: 404,
        statusText: 'Not Found'
      });
      // Cause all Observables to complete and check the results
      tick();
      expect(errorReply).toBe('Unexpected error at service while trying to fetch instruments. Please try again later!');
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorResp = errorHandlerSpy.calls.argsFor(0)[0];
      expect(errorResp.status).toBe(404);
    })));

  it('should handle a network error', inject(
    [PriceService],
    fakeAsync((service: PriceService) => {
      let errorResp: HttpErrorResponse;
      let errorReply: string = '';
      const errorHandlerSpy = spyOn(service, 'handleError').and.callThrough();
      service.getPricesFromFMTS().subscribe({
        next: () => fail('Should fail'),
        error: (err) => (errorReply = err),
      });
      const req = httpTestingController.expectOne(service.url);
      expect(req.request.method).toEqual('GET');
      const error = new ProgressEvent('Network Error');
      req.error(error);
      httpTestingController.verify();
      tick();
      expect(errorReply).toBe(
        'Unexpected error at service while trying to fetch instruments. Please try again later!'
      );
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorResp = errorHandlerSpy.calls.argsFor(0)[0];
    })
  ));
});
