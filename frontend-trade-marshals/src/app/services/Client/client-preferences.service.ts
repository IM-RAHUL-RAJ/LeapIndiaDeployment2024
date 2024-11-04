import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { ClientPreferences } from 'src/app/models/Client/ClientPreferences';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class ClientPreferencesService {

  dataURL = `${URLS.BASEURL}client-preferences/`;


  constructor(private http: HttpClient) { 
  }

  getClientPreferences(clientId: string): Observable<ClientPreferences>{
    return this.http.get<ClientPreferences>(this.dataURL + clientId)
      .pipe(catchError(this.handleError))
  }

  updateClientPreferences(updatedRecord: ClientPreferences):Observable<ClientPreferences> {
    return this.http.put<ClientPreferences>(this.dataURL, updatedRecord)
      .pipe(catchError(this.handleError))
  }

  setClientPreferences(record: ClientPreferences ): Observable<ClientPreferences> {
    return this.http.post<ClientPreferences>(this.dataURL, record)
      .pipe(catchError(this.handleError))
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
        () => 'Unexpected error occurred at the service. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }

}
