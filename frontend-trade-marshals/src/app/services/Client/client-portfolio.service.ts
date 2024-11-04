import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { ClientPortfolio } from 'src/app/models/Client/ClientPortfolio';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class ClientPortfolioService {

  dataURL = `${URLS.BASEURL}portfolio/client/`;

  constructor(private http: HttpClient) { }

  // To retrieve a particular client's portfolio data by providing client ID
  getClientPortfolio(clientId: string): Observable<ClientPortfolio>{
    return this.http.get<ClientPortfolio>(this.dataURL + clientId).pipe(catchError(this.handleError));
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
        () => 'Unexpected error at service while trying to retrieve portfolio. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }
  
}
