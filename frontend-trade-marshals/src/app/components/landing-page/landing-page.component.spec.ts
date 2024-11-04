import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingPageComponent } from './landing-page.component';
import { MaterialModule } from 'src/app/material.module';
import { of, throwError } from 'rxjs';
import { LoginService } from 'src/app/services/Client/login.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { Client } from 'src/app/models/Client/Client';
import { ValidatedClient } from 'src/app/models/Client/ValidatedClient';
import { DatePipe } from '@angular/common';

const testClient: Client = {
  "email": "sowmya@gmail.com",
  "clientId": "1654658069",
  "password": "Marsh2024",
  "name": "Sowmya",
  "dateOfBirth": "11/12/2002",
  "country": "India",
  "identification": [
    {
      "type": "Aadhar",
      "value": "123456789102"
    }
  ],
  "isAdmin": true
}

const testClientProfile: ClientProfile =
{
  "client": {
    "email": "sowmya@gmail.com",
    "clientId": "1654658069",
    "password": "Marsh2024",
    "name": "Sowmya",
    "dateOfBirth": "11/12/2002",
    "country": "India",
    "identification": [
      {
        "type": "Aadhar",
        "value": "123456789102"
      }
    ],
    "isAdmin": true
  },
  "token": 123456789102
}

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  let router: Router;

  let loginMockService: any
  let mockGetClientSpy: any
  let clientProfileMockService: any
  let mockClientProfileSetSpy: any
  
  let datePipe: DatePipe
  let snackBar: any
  let snackBarSpy: any

  beforeEach(async () => {

    loginMockService = jasmine.createSpyObj('LoginService', ['loginClient']);
    mockGetClientSpy = loginMockService.loginClient.and.returnValue(of(testClientProfile));

    clientProfileMockService = jasmine.createSpyObj('ClientProfileService', ['setClientProfile']);
    mockClientProfileSetSpy = clientProfileMockService.setClientProfile.and.callFake((param: any) => { return of(param) })

    snackBar = jasmine.createSpyObj('MatSnackBar', ['open']); //Spying on MatSnackBar

    await TestBed.configureTestingModule({
      declarations: [LandingPageComponent],
      imports: [
        MaterialModule
      ],
      providers: [
        DatePipe,
        { provide: LoginService, useValue: loginMockService },
        { provide: ClientProfileService, useValue: clientProfileMockService },
        { provide: Router, useClass: class { navigateByUrl = jasmine.createSpy('navigateByUrl'); } },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    datePipe = TestBed.inject(DatePipe)
    snackBarSpy = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //Check for landing page header
  it('header should have the title "Trade Marshals"', () => {
    const titleElement = fixture.debugElement.query(By.css('.nav-title')).nativeElement;
    expect(titleElement.textContent).toBe('Trade Marshals');
  });

  //Checking for signup and login button actions
  it('should redirect to registration page when "Sign Up" button is clicked', () => {
    const signUpButton = fixture.debugElement.queryAll(By.css('article button'))[0].nativeElement;
    signUpButton.click();
    fixture.detectChanges();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/register');
  });

  it('should open login popup when "Login" button is clicked', () => {
    spyOn(component, 'openDialog');
    const loginButton = fixture.debugElement.queryAll(By.css('article button'))[1].nativeElement;
    loginButton.click();
    fixture.detectChanges();
    expect(component.openDialog).toHaveBeenCalledWith('loginFormTemplate');
  });


  /*LOGIN FORM SUBMISSION*/

  it('should validate form control', () => {
    const emailCtrl = component.loginCredentials.get('email');
    const passwordCtrl = component.loginCredentials.get('password');
    emailCtrl?.setValue('');
    passwordCtrl?.setValue('')
    expect(component.loginCredentials.valid).toBeFalsy();
    emailCtrl?.setValue('sowmya@gmail.com');
    passwordCtrl?.setValue('Password123')
    expect(component.loginCredentials.valid).toBeTruthy();
  });

  /*LOGIN */

  //There should be an error message when user does not exist
  it("should show snackbar message when logging in user does not exist - invalid email", () => {
    const loginCredentials = { email: "non-existent@gmail.com", password: 'password' };
    component.loginCredentials.setValue(loginCredentials);
    loginMockService.loginClient.and.returnValue(of(null)); //Return null from service
    component.onSubmitLoginForm(); //On submitting login form
    expect(snackBarSpy.open).toHaveBeenCalled();
  });

  //There should be an error message when password is incorrect
  it("should show snackbar message when password is incorrect", () => {
    const loginCredentials = { email: testClient.email, password: 'incorrect-password' }; //Actual testClient password: incorrect-password
    component.loginCredentials.setValue(loginCredentials);
    component.onSubmitLoginForm(); //On submitting login form
    expect(mockGetClientSpy).toHaveBeenCalled() //Service should have called
    expect(snackBarSpy.open).toHaveBeenCalled();
  });

  //There should be a success message and redirection when user logs in successfully
  it("should show snackbar message when user credentails are valid and user logs in successfully", () => {
    const loginCredentials = { email: testClient.email, password: testClient.password }; //Actual testClient password: incorrect-password
    component.loginCredentials.setValue(loginCredentials);
    component.onSubmitLoginForm(); //On submitting login form
    expect(mockGetClientSpy).toHaveBeenCalled() //Service should have called
    expect(mockClientProfileSetSpy).toHaveBeenCalledWith(testClientProfile)
    expect(snackBarSpy.open).toHaveBeenCalled();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/home');
  });

});
