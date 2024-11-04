import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { LoginService } from './login.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';


describe('LoginService', () => {
  let service: LoginService;
  let httpTestingController: HttpTestingController

  let testClients: ClientProfile[] = [] //Test valid Clients

  beforeEach(() => {
    testClients = [
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

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(LoginService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return client from restful service given email ID of a client that exists', inject([LoginService],
    fakeAsync((service: LoginService) => {
      let clientData: ClientProfile | null
      service.loginClient("sowmya@gmail.com", "Marsh2024")
        .subscribe(data => {
          clientData = data;
          expect(clientData).not.toBeNull()
          if(clientData)
            expect(clientData.client?.email).toEqual(testClients[0].client?.email); //Expect that returned client's email matches
        }
        );
      const req = httpTestingController.expectOne(service.loginUrl+ "?email=sowmya@gmail.com&password=Marsh2024");    //Checking if the url is right
      expect(req.request.method).toEqual('GET');      // Assert that the request is a GET
      req.flush(testClients[0]); // Respond with mock data, causing Observable to resolve 
      tick();

    })));

  /*TESTING FOR FAILURES*/
  //Test to check if our service handles a 404 error - TESTING FOR FAILURES
  it('should handle a 404 error', inject([LoginService],
    fakeAsync((service: LoginService) => {
      let errorReply: HttpErrorResponse;
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.loginClient('sample@gmail.com', 'password')
        .subscribe({
          next: () => fail('Should fail'),
          error: (err) => errorReply = err
        });
      const req = httpTestingController.expectOne(service.loginUrl+"?email=sample@gmail.com&password=password"); //Checking if url is right
      expect(req.request.method).toEqual('GET'); //Checking if method is right
      //Flushing with error
      req.flush('Forced 404', {
        status: 404,
        statusText: 'Not Found'
      });
      httpTestingController.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
      errorReply = errorHandlerSpy.calls.argsFor(0)[0];
      expect(errorReply.status).toBe(404);
    })));

  //Test to check if our service handles network error - TESTING FOR FAILURES
  it('should handle a network error', inject([LoginService],
    fakeAsync((service: LoginService) => {
      let errorReply: string = '';
      const errorHandlerSpy = spyOn(service, 'handleError')
        .and.callThrough();
      service.loginClient('sample@gmail.com', 'password')
        .subscribe({
          next: () => fail('Should fail'),
          error: (err) => errorReply = err
        });
      const req = httpTestingController.expectOne(service.loginUrl+"?email=sample@gmail.com&password=password"); //Checking if url is right
      expect(req.request.method).toEqual('GET'); //Checking if method is right
      //Flushing with error
      const error = new ProgressEvent('Network Error')
      req.error(error)
      httpTestingController.verify()
      tick();
      expect(errorHandlerSpy).toHaveBeenCalled();
    })));


});
