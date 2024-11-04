import axios from 'axios';
import { Request, Response } from 'express';
import defaultConfig from '../constants';
import { Client } from '../models/Client/Client';

export class ClientRestController {

    backendApiUrl: string;
    constructor() {
        this.backendApiUrl = `${defaultConfig.BACKEND_URL}/client/`
    }

    //Verifying Client Email
    async verifyClientEmail(req: Request, res: Response) {
        console.log(`Request for Verify Client Email: ${JSON.stringify(req.params, null, 2)}`);
        try {
            // Read email from the URL
            const email = req.params.email;
            if (email) {
                try {
                    const response = await axios.get(`${this.backendApiUrl}verify-email/${email}`);
                    console.log(`Response for Verify Client Email: ${response}`);
                    res.json(response.data) //Success 200 response
                } catch (err) {
                    if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                        console.log('Error from backend while verifying client email: ', err.response)
                        const { status, data } = err.response;
                        const errorMessage = data.message || 'An error occurred while verifying the client email';
                        const errorStatus = status || 500;
                        res.status(errorStatus).json({
                            status: errorStatus,
                            message: errorMessage,
                        });
                    } else {
                        res.status(500).json({
                            status: 500,
                            message: 'Unexpected error in backend service while verifying client email',
                        });
                    }
                }
            }
            else {
                res.status(400).json({ status: 400, message: 'Email cannot be null' })
            }
        } catch (err) {
            res.status(500).json({
                status: 500,
                message: 'Unexpected error in midtier while verifying client email',
            });
        }
    }

    //Registering new Client
    async registerNewClient(req: Request, res: Response) {
        console.log(`Request for Registering New Client: ${JSON.stringify(req.body, null, 2)}`);
        // Read request body
        try {
            const newClient: Client = req.body;
            if (newClient.email && newClient.password && newClient.name && newClient.identification) {
                try {
                    const response = await axios.post(`${this.backendApiUrl}register`, newClient);
                    console.log(`Response for Registering New Client: ${response}`);
                    res.json(response.data) //Success 200 response
                } catch (err) {
                    if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                        //console.log('Error from backend while registering new client: ', err.response)
                        const { status, data } = err.response;
                        const errorMessage = data.message || 'An error occurred while registering the new client';
                        const errorStatus = status || 500;
                        res.status(errorStatus).json({
                            status: errorStatus,
                            message: errorMessage,
                        });
                    } else {
                        res.status(500).json({
                            status: 500,
                            message: 'Unexpected error in backend service while registering new client',
                        });
                    }
                }
            }
            else {
                res.status(400).json({ status: 400, message: 'Few Client Details are null' })
            }
        } catch (err) {
            res.status(500).json({
                status: 500,
                message: 'Unexpected error in midtier while registering new client',
            });
        }

    }

    //Logging in Client
    async loginExistingClient(req: Request, res: Response) {
        console.log(`Request for Login Existing Client: ${JSON.stringify(req.query, null, 2)}`);
        try {
            // Read email and password from query params
            let email = Array.isArray(req.query.email) ? req.query.email[0] : req.query.email;
            let password = Array.isArray(req.query.password) ? req.query.password[0] : req.query.password;
            // console.log(typeof email)
            console.log(`Email: ${email} Password: ${password}`)
            if (email && password) {
                try {
                    const response = await axios.get(`${this.backendApiUrl}?email=${email}&password=${password}`);
                    console.log(`Response for Login Existing Client: ${response}`);
                    res.json(response.data) //Success 200 response
                } catch (err) {
                    if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                        //console.log('Error from backend while logging in client: ', err.response)
                        const { status, data } = err.response;
                        const errorMessage = data.message || 'An error occurred while logging in client';
                        const errorStatus = status || 500;
                        res.status(errorStatus).json({
                            status: errorStatus,
                            message: errorMessage,
                        });
                    } else {
                        res.status(500).json({
                            status: 500,
                            message: 'Unexpected error in backend service while logging in client',
                        });
                    }
                }
            }
            else {
                res.status(400).json({ status: 400, message: 'Login Credentials of Client cannot be null' })
            }
        } catch (err) {
            res.status(500).json({
                status: 500,
                message: 'Unexpected error in midtier while logging in client',
            });
        }
    }

}