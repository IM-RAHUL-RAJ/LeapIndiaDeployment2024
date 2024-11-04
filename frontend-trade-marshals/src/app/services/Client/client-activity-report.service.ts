import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class ClientActivityReportService {

  private dataURL: string = `${URLS.BASEURL}activity-report/`;

  constructor(private http: HttpClient) { }

  // get client trade report
  getClientTradeReport(clientId: string): Observable<any>{
    return this.http.get<any>(this.dataURL + "trades/" + clientId)
    .pipe(catchError(this.handleError));
  }

  // get client profit loss report
  getClientProfitLossReport(clientId: string): Observable<any>{
    return this.http.get<any>(this.dataURL + "pl/" + clientId)
    .pipe(catchError(this.handleError));
  }

  // get client holdings report
  getClientHoldingsReport(clientId: string): Observable<any>{
    return this.http.get<any>(this.dataURL + "holdings/" + clientId)
    .pipe(catchError(this.handleError));
  }

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
        () => 'Unexpected error at service while trying to retrieve client-activity report. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }
}
