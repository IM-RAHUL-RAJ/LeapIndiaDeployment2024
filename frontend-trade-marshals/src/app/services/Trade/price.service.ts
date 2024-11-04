import { inject, Injectable } from '@angular/core';
import { Price } from '../../models/Trade/price';
import { catchError, Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class PriceService {
  prices: Price[] = [];
  url = `${URLS.BASEURL}trade/live-prices`;

  constructor(private httpClient: HttpClient) { }

  getPricesFromFMTS(): Observable<Price[]> {
    return this.httpClient.get<Price[]>(this.url).pipe(catchError(this.handleError));
  }

  handleError(response: HttpErrorResponse) {
    if (response.error instanceof ProgressEvent) {
      console.error('There is a client-side or network error - ' +
        `${response.message} ${response.status} ${response.statusText}`);
    } else {
      console.error(`There is an error with status: ${response.status}, ` +
        `and body: ${JSON.stringify(response.error)}`);
    }
    return throwError(
      () => 'Unexpected error at service while trying to fetch instruments. Please try again later!');
  }

  //Getting Live Prices - Called from portfolio, buy, sell component
  getLivePrices(): Observable<Price[]> {
    let prices = localStorage.getItem('livePrices')!
    return of(JSON.parse(prices))
  }

  //Setting Live Prices - Called from Price List Component
  setLivePrices(prices:Price[]) {
    localStorage.setItem('livePrices', JSON.stringify(prices) )
  }

  //Removing Live Prices from local storage on Logout
  removeLivePrices(){
    localStorage.removeItem('livePrices')
  }

}


