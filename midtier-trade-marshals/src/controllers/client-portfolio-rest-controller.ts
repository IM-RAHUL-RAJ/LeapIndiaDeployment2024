import axios from 'axios';
import { Request, Response } from 'express';
import defaultConfig from '../constants';
 
export class ClientPortfolioRestController {
 
    backendApiUrl: string;
    constructor() {
        this.backendApiUrl = `${defaultConfig.BACKEND_URL}/portfolio/`
    }
 
    // Get client holdings report
    async getClientPortfolio(req: Request, res: Response) {
 
        // Read clientId from URL
        const clientId = req.params.clientId;
 
        if (clientId) {
            try {
                const response = await axios.get(`${this.backendApiUrl}client/${clientId}`);
                console.log(`Response for Get client portfolio: ${response}`);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while fetching client portfolio: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while fetching client portfolio';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while fetching client portfolio',
                    });
                }
            }
        }
        else {
            res.status(400).json({ status: 400, message: 'ClientId cannot be null' })
        }
    }
 
}