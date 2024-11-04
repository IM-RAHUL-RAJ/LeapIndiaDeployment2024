import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { Trade } from '../../models/Trade/trade';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Order } from '../../models/Trade/order';
import { DatePipe } from '@angular/common';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class TradeHistoryService {

  // trades: Trade[] = mockTrades;
  private _snackBar = inject(MatSnackBar);
  url = `${URLS.BASEURL}trade/trade-history/`;

  constructor(private httpClient: HttpClient, private datePipe: DatePipe) { }

  getTrades(clientId: string): Observable<any[]> {
    // return of(this.trades);
    return this.httpClient.get<Trade[]>(this.url + clientId).pipe(
      tap(trades => { 
        console.log(trades);
        // trades.forEach(trade => {
        //   if (trade.executedAt) {
            
        //   }
        // });
      }),
      catchError(this.handleError)
    )
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
    if(response.status == 500 || response.status==0 ){
      return throwError(
        () => 'Unexpected error at service while trying to fetch trade history of user. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }
}

