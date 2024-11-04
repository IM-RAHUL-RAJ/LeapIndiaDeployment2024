import axios from 'axios';
import { Request, Response } from 'express';
import { ClientRestController } from '../../src/controllers/client-rest-controller';
import defaultConfig from '../../src/constants';
import { Client } from '../../src/models/Client/Client';
import { ClientProfile } from '../../src/models/Client/ClientProfile';
// import { beforeEach, describe } from 'node:test';

const testExistingClient: Client = {
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
const testExistingClientProfile: ClientProfile = {
    "client":testExistingClient,
    "token": 1654658069
}

const testNewClient: Client = {
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
const testNewClientProfile: ClientProfile = {
    "client":testNewClient,
    "token": 767836496
}

let backendUrl: string = `${defaultConfig.BACKEND_URL}client`

describe('Client Rest Controller Unit Tests', () => {
    let controller: ClientRestController;
    let mockAxiosGet: any
    let mockAxiosPost: any
    let mockBackendResponse: any
    let mockHttpRequest: Partial<Request>;
    let mockHttpResponse: Partial<Response>;

    beforeEach(() => {
        controller = new ClientRestController();
        mockAxiosGet = spyOn(axios, 'get').and.callThrough();
        mockAxiosPost = spyOn(axios, 'post').and.callThrough();
        mockHttpResponse = {
            status: jasmine.createSpy('status').and.callFake(() => mockHttpResponse), // Mock status method
            json: jasmine.createSpy('json'), send: jasmine.createSpy('send')
        };
    });

    //Verify Client Email
    describe('Verify Client Email test', () => {
        it('returns success 200 when backend returns the same when valid client email is verified', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = { status: 200, data: { isVerified: true } };
            mockAxiosGet.and.returnValue(Promise.resolve(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = { params: { email: testExistingClient.email } };
            await controller.verifyClientEmail(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.json).toHaveBeenCalledWith({ isVerified: true });
        });

        it('returns 406 when backend throws the same when invalid client email is passed', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = {
                isAxiosError: true, // Indicates that this is an Axios error
                response: {
                    status: 406,
                    data: { message: 'Client email is invalid' }
                }
            };
            mockAxiosGet.and.returnValue(Promise.reject(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = { params: { email: 'invalid-email' } };
            await controller.verifyClientEmail(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(406);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 406, message: mockBackendResponse.response.data.message});
        });

        it('returns 500 when backend throws Unexpected error', async () => {
            //Mocking the response sent by axios
            mockAxiosGet.and.returnValue(Promise.reject(new Error('Unexpected Error')));

            //Mocking request and response
            mockHttpRequest = { params: { email: 'invalid-email' } };
            await controller.verifyClientEmail(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(500);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 500, message: 'Unexpected error in backend service while verifying client email'});
        });
    });

     //Register New Client
     describe('Register New Client test', () => {
        it('returns success 200 when backend returns the same when valid client is registered', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = { status: 200, data: testNewClientProfile };
            mockAxiosPost.and.returnValue(Promise.resolve(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = { body: testNewClient };
            await controller.registerNewClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.json).toHaveBeenCalledWith(testNewClientProfile);
        });

        it('returns 400 when few client details are empty in request', async () => {
            //Mocking request and response
            mockHttpRequest = { body: {'clientId':'','email':'','password':''} };
            await controller.registerNewClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(400);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 400, message: 'Few Client Details are null'});
        });

        it('returns 404 when backend throws the same when existing client is registered', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = {
                isAxiosError: true, // Indicates that this is an Axios error
                response: {
                    status: 404,
                    data: { message: 'Client is already registered' }
                }
            };
            mockAxiosPost.and.returnValue(Promise.reject(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = { body: testExistingClient};
            await controller.registerNewClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(404);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 404, message: mockBackendResponse.response.data.message});
        });

        it('returns 500 when backend throws Unexpected error', async () => {
            //Mocking the response sent by axios
            mockAxiosPost.and.returnValue(Promise.reject(new Error('Unexpected Error')));

            //Mocking request and response
            mockHttpRequest = { body: testExistingClient};
            await controller.registerNewClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(500);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 500, message: 'Unexpected error in backend service while registering new client'});
        });
    });

    //Login Existing Client
    describe('Login Existing Client test', () => {
        it('returns success 200 when backend returns the same when existing client is logged in', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = { status: 200, data: testExistingClientProfile };
            mockAxiosGet.and.returnValue(Promise.resolve(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = { query: { email: [testExistingClient.email], password: [testExistingClient.password] } };
            await controller.loginExistingClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.json).toHaveBeenCalledWith(testExistingClientProfile);
        });

        it('returns 400 when few client details are empty in request', async () => {
            //Mocking request and response
            mockHttpRequest = { query: { email: [''], password: [''] } };
            await controller.loginExistingClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(400);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 400, message: 'Login Credentials of Client cannot be null'});
        });

        it('returns 406 when backend throws the same when a non existing client cannot be logged in', async () => {
            //Mocking the response sent by axios
            mockBackendResponse = {
                isAxiosError: true, // Indicates that this is an Axios error
                response: {
                    status: 406,
                    data: { message: 'Logging in Client doesnt exist' }
                }
            };
            mockAxiosGet.and.returnValue(Promise.reject(mockBackendResponse));

            //Mocking request and response
            mockHttpRequest = {  query: { email: [testNewClient.email], password: [testNewClient.password] } };
            await controller.loginExistingClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(406);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 406, message: mockBackendResponse.response.data.message});
        });

        it('returns 500 when backend throws Unexpected error', async () => {
            //Mocking the response sent by axios
            mockAxiosGet.and.returnValue(Promise.reject(new Error('Unexpected Error')));

            //Mocking request and response
            mockHttpRequest = {  query: { email: [testNewClient.email], password: [testNewClient.password] } };
            await controller.loginExistingClient(mockHttpRequest as Request, mockHttpResponse as Response);

            expect(mockHttpResponse.status).toHaveBeenCalledWith(500);
            expect(mockHttpResponse.json).toHaveBeenCalledWith({status: 500, message: 'Unexpected error in backend service while logging in client'});
        });
    });

});

