import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { RegisterService } from './register.service';
import { Client } from 'src/app/models/Client/Client';
import { ClientPortfolio } from 'src/app/models/Client/ClientPortfolio';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';

describe('RegisterService', () => {
  let service: RegisterService;
  let httpTestingController: HttpTestingController
  let testClients: ClientProfile[] = [] //Test Clients
  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(RegisterService);
    httpTestingController = TestBed.inject(HttpTestingController);

    testClients = [
      {
        "client": {
          "email": "sam@gmail.com",
          "clientId": "767836496",
          "password": "Marsh2024",
          "name": "Sam",
          "dateOfBirth": "11/12/2002",
          "country": "USA",
          "identification": [
            {
              "type": "SSN",
              "value": "1643846323"
            }
          ],
          "isAdmin": false
        },
        "token": 1643846323
      },
      {
        "client": {
          "email": "sowmya@gmail.com",
          "clientId": "1654658069",
          "password": "Marsh2024",
          "name": "Sowmya",
          "dateOfBirth": "11/12/2002",
          "country": "India",
          "identification": [
            {
              "type": "Aadhar",
              "value": "123456789102"
            }
          ],
          "isAdmin": true
        },
        "token": 123456789102
      }
    ]
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should save a new client with a POST restful service request', inject([RegisterService],
    fakeAsync((service: RegisterService) => {
      let newClient: Client = {
        "email": "sam@gmail.com",
        "clientId": "767836496",
        "password": "Marsh2024",
        "name": "Sam",
        "dateOfBirth": "11/12/2002",
        "country": "USA",
        "identification": [
          {
            "type": "SSN",
            "value": "1643846323"
          }
        ],
        "isAdmin": false
      }
      service.saveClientDetails(newClient).subscribe();
      const req = httpTestingController.expectOne(service.clientDataURL);    //Checking if the url is right
      expect(req.request.method).toEqual('POST');      // Assert that the request is a POST
      expect(req.request.body).toEqual(testClients[0].client); //Expect that the posted client is same as the sent one
    })));

  /*TESTING FOR FAILURES*/
  //Test to check if our service handles a 404 error - TESTING FOR FAILURES
  it('should handle a 404 error', inject([RegisterService],
    fakeAsync((service: RegisterService) => {
      const expectedClient: Client = {
        "email": "sowmya@gmail.com",
        "clientId": "1654658069",
        "password": "Marsh2024",
        "name": "Sowmya",
        "dateOfBirth": "11/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": true
      }
      let errorResp: HttpErrorResponse;
      let errorReply: string = '';
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.saveClientDetails(expectedClient) //Trying to save a new client
        .subscribe({
          next: () => fail('Should fail'),
          error: (err) => errorReply = err
        });
      const req = httpTestingController.expectOne(service.clientDataURL); //Checking if url is right
      expect(req.request.method).toEqual('POST'); //Checking if method is right
      //Flushing with error
      req.flush('Forced 404', {
        status: 404,
        statusText: 'Not Found'
      });
      httpTestingController.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorResp = errorHandlerSpy.calls.argsFor(0)[0];
      expect(errorResp.status).toBe(404);
    })));

  //Test to check if our service handles network error - TESTING FOR FAILURES
  it('should handle a network error', inject([RegisterService],
    fakeAsync((service: RegisterService) => {
      const expectedClient: Client = {
        "email": "sowmya@gmail.com",
        "clientId": "1654658069",
        "password": "Marsh2024",
        "name": "Sowmya",
        "dateOfBirth": "11/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": true
      }
      let errorResp: HttpErrorResponse;
      let errorReply: string = '';
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.saveClientDetails(expectedClient) //Trying to save a new client'S Portfolio
        .subscribe({
          next: () => fail('Should fail'),
          error: (err) => errorReply = err
        });
      const req = httpTestingController.expectOne(service.clientDataURL); //Checking if url is right
      expect(req.request.method).toEqual('POST'); //Checking if method is right
      //Flushing with error
      const error = new ProgressEvent('Network Error')
      req.error(error)
      httpTestingController.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
    })));
});