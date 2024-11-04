import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClientPreferencesService } from '../Client/client-preferences.service';
import { ClientProfileService } from '../Client/client-profile.service';
import { catchError, Observable, throwError } from 'rxjs';
import { Price } from '../../models/Trade/price';
import { ClientPreferences } from '../../models/Client/ClientPreferences';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class RoboAdvisorService {

  url = `${URLS.BASEURL}trade/`

  constructor(private httpClient: HttpClient) { }
  
  getTopBuyTrades(clientPreferences: ClientPreferences): Observable<Price[]>{
      const headers = new HttpHeaders({
        'Content-Type': 'application/json'
      });
      let apiUrl = this.url + 'suggest-buy';
      return this.httpClient.post<Price[]>(apiUrl, clientPreferences, {headers}).pipe(catchError(this.handleError));
  }

  getTopSellTrades(clientPreferences: ClientPreferences): Observable<Price[]>{
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    let apiUrl = this.url + 'suggest-sell';
    return this.httpClient.post<Price[]>(apiUrl, clientPreferences, {headers}).pipe(catchError(this.handleError));
  }

  //Function to handle errors
  handleError(response: HttpErrorResponse) {
    if (response.error instanceof ProgressEvent) {
      console.error('There is a client-side or network error - ' +
        `${response.message} ${response.status} ${response.statusText}`);
    } else {
      console.error(`There is an error with status: ${response.status}, ` +
        `and body: ${JSON.stringify(response.error)}`);
    }
    if(response.status == 500 || response.status == 0){
      return throwError(
        () => 'Unexpected error at service while trying to retrieve robo advisor recommendations. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }
  
}
