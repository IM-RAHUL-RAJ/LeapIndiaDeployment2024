import axios from 'axios';
import { Request, Response } from 'express';
import defaultConfig from '../constants';
 
export class ClientActivityReportRestController{
 
    backendApiUrl: string;
    constructor() {
        this.backendApiUrl = `${defaultConfig.BACKEND_URL}/activity-report/`
    }
 
    // Get client holdings report
    async getClientHoldingsReport(req: Request, res: Response){
 
        // Read clientId from URL
        const clientId = req.params.clientId;
 
        if (clientId) {
            try {
                const response = await axios.get(`${this.backendApiUrl}holdings/${clientId}`);
                console.log(`Response for Get client holdings report: ${response}`);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while fetching client holdings report: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while fetching client holdings report';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while fetching client holdings report',
                    });
                }
            }
        }
        else {
            res.status(400).json({ status: 400, message: 'ClientId cannot be null' })
        }
    }
 
    // Get client trades report
    async getClientTradesReport(req: Request, res: Response){
 
        // Read clientId from URL
        const clientId = req.params.clientId;
 
        if (clientId) {
            try {
                const response = await axios.get(`${this.backendApiUrl}trades/${clientId}`);
                console.log(`Response for Get client trades report: ${response}`);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while fetching client trades report: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while fetching client trades report';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while fetching client trades report',
                    });
                }
            }
        }
        else {
            res.status(400).json({ status: 400, message: 'ClientId cannot be null' })
        }
    }
 
    // Get client P&L report
    async getClientPLReport(req: Request, res: Response){
 
        // Read clientId from URL
        const clientId = req.params.clientId;
 
        if (clientId) {
            try {
                const response = await axios.get(`${this.backendApiUrl}pl/${clientId}`);
                console.log(`Response for Get client P&L report: ${response}`);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while fetching client P&L report: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while fetching client P&L report';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while fetching client P&L report',
                    });
                }
            }
        }
        else {
            res.status(400).json({ status: 400, message: 'ClientId cannot be null' })
        }
    }
}