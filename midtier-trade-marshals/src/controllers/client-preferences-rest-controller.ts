import { Request, Response } from "express-serve-static-core";
import { ParsedQs } from "qs";
import defaultConfig from "../constants";
import axios from "axios";
import { ClientPreferences } from "../models/Client/ClientPreferences";

export class ClientPreferencesRestController{
    
    backendApiUrl: string;
    constructor(){
        this.backendApiUrl = `${defaultConfig.BACKEND_URL}client-preferences/`
    }

    async getClientPreferencesById(req: Request, res: Response) {
        console.log(`Request for get Client Preferences:`, req);
        const id = req.params.id
        if(id){
            try{
                const response = await axios.get(`${this.backendApiUrl}${id}`)
                console.log(`Response for Getting Client Preferences:`, response);
                res.json(response.data)
            }
            catch(err){
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while getting client preferences: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while getting client preferences';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while getting client preferences',
                    });
                }
            }
        }
    }

    async addClientPreferences(req: Request, res: Response) {
        console.log(`Request for Add Client Preferences:`, req);
        const newClientPreferences: ClientPreferences = req.body;

        try {
            const response = await axios.post(`${this.backendApiUrl}`,newClientPreferences);
            console.log(`Response for Add Client Preferences:`, response);
            res.json(response.data) //Success 200 response
        } catch (err) {
            if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                console.log('Error from backend while adding new client preferences: ', err.response)
                const { status, data } = err.response;
                const errorMessage = data.message || 'An error occurred while adding new client preferences:';
                const errorStatus = status || 500;
                res.status(errorStatus).json({
                    status: errorStatus,
                    message: errorMessage,
                });
            } else {
                res.status(500).json({
                    status: 500,
                    message: 'Unexpected error in backend service while adding new client preferences:',
                });
            }
        }
    }

    async updateClientPreferences(req: Request, res: Response) {
        console.log(`Request for Update Client Preferences:`, req);

        const updatedClientPreferences: ClientPreferences = req.body;
        if(updatedClientPreferences.clientId && updatedClientPreferences.riskTolerance){
            try {
                const response = await axios.put(`${this.backendApiUrl}`,updatedClientPreferences);
                console.log(`Response for Update Client Preferences:`, response);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while updating Client Preferences: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while updating Client Preferences:';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while updating Client Preferences:',
                    });
                }
            }
        }
    }
}