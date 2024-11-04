import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { ClientProfileService } from './client-profile.service';
import { ValidatedClient } from 'src/app/models/Client/ValidatedClient';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';


describe('ClientProfileService', () => {
  let service: ClientProfileService;
  let httpTestingController: HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ClientProfileService);
    httpTestingController = TestBed.inject(HttpTestingController)
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // it('fmts should verify client data successfully', () => {
  //   const clientData = { clientId: '1234567890', email: 'sample.client@gmail.com' };
  //   const validatedClient: ValidatedClient = { clientId: '1234567890', email: 'sample.client@gmail.com', token: 1237645797 };
  //   let validResponse: any
  //   service.fmtsClientVerification(clientData).subscribe(data => { validResponse = data });

  //   const req = httpTestingController.expectOne(service.fmtsURL);
  //   expect(req.request.method).toBe('POST');
  //   req.flush(validatedClient);
  //   expect(req.request.body).toBe(clientData) //Check if request sent is right
  //   expect(validResponse).toBe(validatedClient) //Check if response received is right
  // });

  // /*TESTING FOR FAILURES*/
  // //Test to check if our service handles a fmts 406 error - TESTING FOR FAILURES
  // it('should handle a 406 error from fmts service', inject([ClientProfileService],
  //   fakeAsync((service: ClientProfileService) => {
  //     const expectedClientData = { clientId: '1234567890', email: 'sample.client@gmail.com' };
  //     let errorResp: HttpErrorResponse;
  //     let errorReply: string = '';
  //     const errorHandlerSpy = spyOn(service, 'handleFMTSError')
  //       .and.callThrough();
  //     service.fmtsClientVerification(expectedClientData) //Trying to validated a client data
  //       .subscribe({
  //         next: () => fail('Should fail'),
  //         error: (err) => errorReply = err
  //       });
  //     const req = httpTestingController.expectOne(service.fmtsURL); //Checking if url is right
  //     expect(req.request.method).toEqual('POST'); //Checking if method is right
  //     //Flushing with error
  //     req.flush('Forced 406', {
  //       status: 406,
  //       statusText: 'Not Acceptable'
  //     });
  //     httpTestingController.verify()
  //     tick();
  //     expect(errorReply).toBe('Client details couldnt be validated! Please try again later!');
  //     expect(errorHandlerSpy).toHaveBeenCalled();
  //     errorResp = errorHandlerSpy.calls.argsFor(0)[0];
  //     expect(errorResp.status).toBe(406);
  //   })));

  // //Test to check if our service handles a 404 error - TESTING FOR FAILURES
  // it('should handle a 404 error', inject([ClientProfileService],
  //   fakeAsync((service: ClientProfileService) => {
  //     const expectedClientData = { clientId: '1234567890', email: 'sample.client@gmail.com' };
  //     let errorResp: HttpErrorResponse;
  //     let errorReply: string = '';
  //     const errorHandlerSpy = spyOn(service, 'handleFMTSError')
  //       .and.callThrough();
  //     service.fmtsClientVerification(expectedClientData) //Trying to validated a client data
  //       .subscribe({
  //         next: () => fail('Should fail'),
  //         error: (err) => errorReply = err
  //       });
  //     const req = httpTestingController.expectOne(service.fmtsURL); //Checking if url is right
  //     expect(req.request.method).toEqual('POST'); //Checking if method is right
  //     //Flushing with error
  //     req.flush('Forced 404', {
  //       status: 404,
  //       statusText: 'Not Found'
  //     });
  //     httpTestingController.verify()
  //     tick();
  //     expect(errorReply).toBe('Unexpected error at service while validating client data. Please try again later!');
  //     expect(errorHandlerSpy).toHaveBeenCalled();
  //     errorResp = errorHandlerSpy.calls.argsFor(0)[0];
  //     expect(errorResp.status).toBe(404);
  //   })));

  // //Test to check if our service handles a network error - TESTING FOR FAILURES
  // it('should handle a network error', inject([ClientProfileService],
  //   fakeAsync((service: ClientProfileService) => {
  //     const expectedClientData = { clientId: '1234567890', email: 'sample.client@gmail.com' };
  //     let errorResp: HttpErrorResponse;
  //     let errorReply: string = '';
  //     const errorHandlerSpy = spyOn(service, 'handleFMTSError')
  //       .and.callThrough();
  //     service.fmtsClientVerification(expectedClientData) //Trying to validated a client data
  //       .subscribe({
  //         next: () => fail('Should fail'),
  //         error: (err) => errorReply = err
  //       });
  //     const req = httpTestingController.expectOne(service.fmtsURL); //Checking if url is right
  //     expect(req.request.method).toEqual('POST'); //Checking if method is right
  //     //Flushing with error
  //     const error = new ProgressEvent('Network Error')
  //     req.error(error)
  //     httpTestingController.verify()
  //     tick();
  //     expect(errorReply).toBe('Unexpected error at service while validating client data. Please try again later!');
  //     expect(errorHandlerSpy).toHaveBeenCalled();
  //   })));

  /*GET CLIENT PROFILE*/
  it('should return client profile from local storage', () => {
    const mockProfile: ClientProfile = {
      'client': {
        "email": "client@gmail.com",
        "clientId": "5413074269",
        "password": "Marsh2024",
        "name": "Client 1",
        "dateOfBirth": "08/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": false
      }, 'token': 1246534566
    };
    localStorage.setItem('clientDetails', JSON.stringify(mockProfile)); //Setting in local storage
    service.getClientProfile().subscribe(profile => { //Getting from service and checking if they are equal
      expect(profile).toEqual(mockProfile);
    });
  });

  it('should set client profile in local storage', () => {
    const mockProfile: ClientProfile = {
      'client': {
        "email": "client@gmail.com",
        "clientId": "5413074269",
        "password": "Marsh2024",
        "name": "Client 1",
        "dateOfBirth": "08/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": false
      }, 'token': 1246534566
    };
    service.setClientProfile(mockProfile) //Setting with service
    const storedProfile = JSON.parse(localStorage.getItem('clientDetails')!); //Getting from local storage
    expect(storedProfile.client.email).toEqual(mockProfile.client?.email); //Check if they are equal
  });

  it('should remove client profile from local storage', () => {
    const mockProfile: ClientProfile = {
      'client': {
        "email": "client@gmail.com",
        "clientId": "5413074269",
        "password": "Marsh2024",
        "name": "Client 1",
        "dateOfBirth": "08/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": false
      }, 'token': 1246534566
    };
    localStorage.setItem('clientDetails', JSON.stringify(mockProfile)); //Setting in local storage
    service.removeClientProfile() //Removing with serivce
    const storedProfile = JSON.parse(localStorage.getItem('clientDetails')!); //Getting from local storage
    expect(storedProfile).toBeFalsy; //Should be null
  });
});

