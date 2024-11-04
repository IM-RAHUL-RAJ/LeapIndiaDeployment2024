import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { catchError, map, Observable } from 'rxjs';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  snackBarConfig = new MatSnackBarConfig();
  constructor(private clientProfileService: ClientProfileService, private router: Router,  private snackBar: MatSnackBar){
    this.snackBarConfig.duration = 3000;
    this.snackBarConfig.panelClass = ['red-snackbar'];
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return this.clientProfileService.getClientProfile().pipe(
      map((data) => {
        if (data) {
          return true; // Allow access if data exists
        } else {
          this.snackBar.open("Please login to continue", '', this.snackBarConfig)
          return this.router.createUrlTree(['/']); // Redirect to landing page if no data
        }
      }),
      catchError(() => {
        this.snackBar.open("Unexpected error occured, Please login again!", '', this.snackBarConfig)
        return [this.router.createUrlTree(['/'])]; // Handle errors by redirecting to landing page
      })
    );
  }
  
}
