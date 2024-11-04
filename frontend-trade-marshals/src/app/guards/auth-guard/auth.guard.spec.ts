import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';

describe('AuthGuard', () => {
  let mockProfile:ClientProfile

  let guard: AuthGuard;
  let clientProfileServiceMock: jasmine.SpyObj<ClientProfileService>;
  let routerMock: jasmine.SpyObj<Router>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    mockProfile = {
      'client': {
        "email": "client@gmail.com",
        "clientId": "5413074269",
        "password": "Marsh2024",
        "name": "Client 1",
        "dateOfBirth": "08/12/2002",
        "country": "India",
        "identification": [
          {
            "type": "Aadhar",
            "value": "123456789102"
          }
        ],
        "isAdmin": false
      }, 'token': 1246534566
    };
    const clientProfileServiceSpy = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
    const routerSpy = jasmine.createSpyObj('Router', ['createUrlTree']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: ClientProfileService, useValue: clientProfileServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
      ],
    });

    guard = TestBed.inject(AuthGuard);
    clientProfileServiceMock = TestBed.inject(ClientProfileService) as jasmine.SpyObj<ClientProfileService>;
    routerMock = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    snackBarMock = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
