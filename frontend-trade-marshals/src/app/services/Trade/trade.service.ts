import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { Trade } from '../../models/Trade/trade';
import { Order } from '../../models/Trade/order';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  url = `${URLS.BASEURL}trade/execute-trade`;

  constructor(private httpClient: HttpClient) { }

  executeTrade(order: Order): Observable<Trade> {
    return this.httpClient.post<Trade>(this.url, order).pipe(catchError(this.handleError));
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
        () => 'Unexpected error at service while trying to save trade. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }
}

