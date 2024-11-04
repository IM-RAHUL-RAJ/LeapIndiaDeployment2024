import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { URLS } from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  loginUrl = `${URLS.BASEURL}client`

  constructor(private http: HttpClient) {
  }

  loginClient(email: String, password: string): Observable<ClientProfile | null>{
    let api = this.loginUrl + "?email=" + email + "&password="+ password;
    return this.http.get<ClientProfile>(api).pipe(
      tap(data => { console.log(data) }),
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
        () => 'Unexpected error at service while trying to login user. Please try again later!'
      );
    }
    return throwError(
      () => response.error.message
    );
  }


}
