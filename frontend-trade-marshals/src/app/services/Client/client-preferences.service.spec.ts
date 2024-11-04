import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClientPreferencesService } from './client-preferences.service';
import { ClientPreferences } from 'src/app/models/Client/ClientPreferences';
import { HttpErrorResponse } from '@angular/common/http';

describe('ClientPreferencesService', () => {
  let service: ClientPreferencesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClientPreferencesService]
    });
    service = TestBed.inject(ClientPreferencesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch client preferences', () => {
    const mockPreferences: ClientPreferences = { clientId: '1654658069', investmentPurpose: 'Retirement', incomeCategory: 'MIG', lengthOfInvestment: 'Medium', percentageOfSpend: 'Tier2', riskTolerance: 3, acceptAdvisor: 'true' };

    service.getClientPreferences('1654658069').subscribe(preferences => {
      expect(preferences).toEqual(mockPreferences);
    });

    const req = httpMock.expectOne(service.dataURL + '1654658069');
    expect(req.request.method).toBe('GET');
    req.flush(mockPreferences);

  });

  it('should handle error when fetching client preferences', inject([ClientPreferencesService],
    fakeAsync((service: ClientPreferencesService) => {
      let errorReply: HttpErrorResponse;
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.getClientPreferences('123').subscribe({
        next: () => fail('expected an error, not preferences'),
        error: (err) => { errorReply = err }
      });
  
      const req = httpMock.expectOne(service.dataURL + '123');
      req.flush('Error', { status: 500, statusText: 'Server Error' });
      httpMock.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorReply = errorHandlerSpy.calls.argsFor(0)[0];
      expect(errorReply.status).toBe(500);
    })));

  it('should update client preferences', () => {
    const mockPreferences: ClientPreferences = { clientId: '1654658069', investmentPurpose: 'Retirement', incomeCategory: 'MIG', lengthOfInvestment: 'Medium', percentageOfSpend: 'Tier2', riskTolerance: 3, acceptAdvisor: 'true' };

    service.updateClientPreferences(mockPreferences).subscribe(preferences => {
      expect(preferences).toEqual(mockPreferences);
    });

    const req = httpMock.expectOne(`${service.dataURL}`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockPreferences);
  });

  it('should handle error when updating client preferences', inject([ClientPreferencesService],
    fakeAsync((service: ClientPreferencesService) => {
      let errorReply: HttpErrorResponse;
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      const mockPreferences: any = { clientId: '1654658069', investmentPurpose: 'Retirement', incomeCategory: 'MIG', lengthOfInvestment: 'Medium', percentageOfSpend: 'Tier2', riskTolerance: 3, acceptAdvisor: 'true' };
      service.updateClientPreferences(mockPreferences).subscribe({
        next: () => fail('expected an error, not preferences'),
        error: (err) => { errorReply = err }
      });
  
      const req = httpMock.expectOne(service.dataURL);
      req.flush('Error', { status: 500, statusText: 'Server Error' });
      httpMock.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorReply = errorHandlerSpy.calls.argsFor(0)[0];
      expect(errorReply.status).toBe(500);
    })));

  it('should set client preferences', () => {
    const mockPreferences: ClientPreferences = { clientId: '1654658069', investmentPurpose: 'Retirement', incomeCategory: 'MIG', lengthOfInvestment: 'Medium', percentageOfSpend: 'Tier2', riskTolerance: 3, acceptAdvisor: 'true' };

    service.setClientPreferences(mockPreferences).subscribe(preferences => {
      expect(preferences).toEqual(mockPreferences);
    });

    const req = httpMock.expectOne(service.dataURL);
    expect(req.request.method).toBe('POST');
    req.flush(mockPreferences);
  });
});
