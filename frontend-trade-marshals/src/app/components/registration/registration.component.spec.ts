import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationComponent } from './registration.component';
import { DatePipe } from '@angular/common';
import { MaterialModule } from 'src/app/material.module';
import { of, throwError } from 'rxjs';
import { LoginService } from 'src/app/services/Client/login.service';
import { RegisterService } from 'src/app/services/Client/register.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimations } from '@angular/platform-browser/animations';
import { Client } from 'src/app/models/Client/Client';
import { ValidatedClient } from 'src/app/models/Client/ValidatedClient';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { Router } from '@angular/router';
import { ClientPortfolio } from 'src/app/models/Client/ClientPortfolio';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

const testExistingClient: Client = {
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

const testClient: Client = {
  "email": "sam@gmail.com",
  "clientId": "767836496",
  "password": "Marsh2024",
  "name": "Sam",
  "dateOfBirth": "11/12/2002",
  "country": "USA",
  "identification": [
    {
      "type": "SSN",
      "value": "1643846323"
    }
  ],
  "isAdmin": false
}

const testExistingClientProfile: ClientProfile =
{
  "client": testExistingClient,
  "token": 1654658069
}

const testClientProfile: ClientProfile =
{
  "client": testClient,
  "token": 1252630773
}

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let datePipe: DatePipe
  let router: Router;

  let clientProfileMockService: any
  let mockClientProfileSetSpy: any
  let registerMockService: any
  let mockVerifyClientEmailSpy: any
  let mockAddClientSpy: any

  let snackBar: any
  let snackBarSpy: any

  beforeEach(async () => {

    clientProfileMockService = jasmine.createSpyObj('ClientProfileService', ['setClientProfile']);
    mockClientProfileSetSpy = clientProfileMockService.setClientProfile.and.callFake((param: any) => { return of(param) })

    registerMockService = jasmine.createSpyObj('RegisterService', ['getVerificationOfClientEmail', 'saveClientDetails']);
    mockVerifyClientEmailSpy = registerMockService.getVerificationOfClientEmail.and.callFake((param: any) => { return of(param) })
    mockAddClientSpy = registerMockService.saveClientDetails.and.callFake((param: any) => { return of(param) })

    snackBar = jasmine.createSpyObj('MatSnackBar', ['open']); //Spying on MatSnackBar

    await TestBed.configureTestingModule({
      declarations: [RegistrationComponent,],
      imports: [
        MaterialModule,
        ReactiveFormsModule
      ],
      providers: [
        DatePipe,
        //{ provide: LoginService, useValue: loginMockService },
        { provide: RegisterService, useValue: registerMockService },
        { provide: ClientProfileService, useValue: clientProfileMockService },
        { provide: Router, useClass: class { navigateByUrl = jasmine.createSpy('navigateByUrl'); } },
        { provide: MatSnackBar, useValue: snackBar },
        provideAnimations()
      ]
    })
      .compileComponents();

    datePipe = TestBed.inject(DatePipe)
    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    snackBarSpy = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /*REGISTRATION FORM VALIDATION*/
  it('should invalidate password if it lacks strength', () => {
    const passwordControl = component.signupForm.get('password');
    passwordControl?.setValue('PASS1');
    passwordControl?.updateValueAndValidity();
    expect(passwordControl?.valid).toBeFalsy();
    expect(passwordControl?.errors?.['passwordStrength']).toBeTruthy();
  });

  it('should invalidate signup form when passwords dont match', () => {
    const passwordControl = component.signupForm.get('password');
    const retypePasswordControl = component.signupForm.get('retypePassword');
    passwordControl?.setValue('Password123');
    retypePasswordControl?.setValue('Password')
    component.signupForm.updateValueAndValidity()
    expect(component.signupForm.errors?.['identityRevealed']).toBeTruthy();
    passwordControl?.setValue('Password123');
    retypePasswordControl?.setValue('Password123')
    component.signupForm.updateValueAndValidity()
    expect(component.signupForm.errors?.['identityRevealed']).toBeFalsy();
  });

  it('should validate signup form control', () => {
    const emailControl = component.signupForm.get('email');
    const passwordControl = component.signupForm.get('password');
    const retypePasswordControl = component.signupForm.get('retypePassword');
    emailControl?.setValue('');
    passwordControl?.setValue('')
    retypePasswordControl?.setValue('')
    expect(component.signupForm.valid).toBeFalsy();
    emailControl?.setValue('sam@gmail.com');
    passwordControl?.setValue('Password123')
    retypePasswordControl?.setValue('Password123')
    expect(component.signupForm.valid).toBeTruthy();
  });

  it('should validate personal details form control', () => {
    const nameControl = component.personalDetails.get('name');
    const doBControl = component.personalDetails.get('doB');
    const countryControl = component.personalDetails.get('country');
    nameControl?.setValue('');
    doBControl?.setValue('')
    countryControl?.setValue('')
    expect(component.personalDetails.valid).toBeFalsy();
    nameControl?.setValue('Sample User');
    doBControl?.setValue(new Date('2002-09-05'))
    countryControl?.setValue('India')
    expect(component.personalDetails.valid).toBeTruthy();
  });

  it('should invalidate a wrong aadhaar format of govt id', () => {
    const typeControl = component.identificationDetails.get('type');
    const valueControl = component.identificationDetails.get('value');
    typeControl?.setValue('Aadhar');
    valueControl?.setValue('123') //Invalid aadhar format
    component.identificationDetails.updateValueAndValidity()
    expect(component.identificationDetails.errors?.['validID']).toBeTruthy();
    valueControl?.setValue('345674830532')
    component.identificationDetails.updateValueAndValidity() //Correct format
    expect(component.identificationDetails.errors?.['validID']).toBeFalsy();
  });

  it('should validate identification details form control', () => {
    const typeControl = component.identificationDetails.get('type');
    const valueControl = component.identificationDetails.get('value');
    typeControl?.setValue('');
    valueControl?.setValue('')
    expect(component.identificationDetails.valid).toBeFalsy();
    typeControl?.setValue('Aadhar');
    valueControl?.setValue('345674830532')
    expect(component.identificationDetails.valid).toBeTruthy();
  });

  /*REGISTRATION*/
  //There should be an error message when user already exists
  it("should show error snackbar message when registering an existing user - existing email", () => {
    const signupForm = { email: "sowmya@gmail.com", password: 'Password123', retypePassword: 'Password123' };
    component.signupForm.setValue(signupForm);
    registerMockService.getVerificationOfClientEmail.and.returnValue(of({isVerified:true}))

    const stepper = { next: jasmine.createSpy('next') } as any;
    component.validateNewEmail(stepper); //On submitting signup form
    expect(mockVerifyClientEmailSpy).toHaveBeenCalledWith("sowmya@gmail.com")
    expect(snackBarSpy.open).toHaveBeenCalled()
    expect(component.signupForm.value).toEqual({ email: null, password: null, retypePassword: null });
    expect(stepper.next).not.toHaveBeenCalled();
  });


  //Registration of client - Successful Email validation of user
  it("should go to the next after successful validation", () => {
    const signupForm = { email: "sample.user@gmail.com", password: 'Password123', retypePassword: 'Password123' };
    component.signupForm.setValue(signupForm);
    registerMockService.getVerificationOfClientEmail.and.returnValue(of({isVerified:false})) //Valid client

    const stepper = { next: jasmine.createSpy('next') } as any;
    component.validateNewEmail(stepper); //On submitting signup form
    expect(mockVerifyClientEmailSpy).toHaveBeenCalled() //Validating client
    expect(stepper.next).toHaveBeenCalled(); //Goes to the next form
  });


  //Successful registration of client but error in saving client details
  it('should show error snackbar message when new client details are right but there is an error in saving client details', () => {
    component.signupForm.setValue({ email: testClient.email, password: testClient.password, retypePassword: testClient.password })
    component.personalDetails.setValue({ name: testClient.name, doB: testClient.dateOfBirth, country: testClient.country })
    component.identificationDetails.setValue({ type: testClient.identification[0].type, value: testClient.identification[0].value });
    registerMockService.saveClientDetails.and.returnValue(throwError(() => new Error('Couldnt register user')));
    
    component.submitRegistrationForm(); //On submitting registration form
    expect(snackBar.open).toHaveBeenCalled();
  })

  //Successful registration of client and setting of client profile
  it('should succesfully register a client given valid details', () => {
    component.signupForm.setValue({ email: testClient.email, password: testClient.password, retypePassword: testClient.password })
    component.personalDetails.setValue({ name: testClient.name, doB: testClient.dateOfBirth, country: testClient.country })
    component.identificationDetails.setValue({ type: testClient.identification[0].type, value: testClient.identification[0].value });
    registerMockService.getVerificationOfClientEmail.and.returnValue(of({isVerified:false})) //Valid client
    registerMockService.saveClientDetails.and.returnValue(of(testClientProfile)) //Valid registration of client

    component.submitRegistrationForm(); //On submitting registration form
    expect(mockAddClientSpy).toHaveBeenCalled(); //Successfully saving Client details
    expect(mockClientProfileSetSpy).toHaveBeenCalledWith(testClientProfile); //Succesfully setting Client Portfolio
    //Now redirecting to client preferences page
    expect(router.navigateByUrl).toHaveBeenCalledWith('/home/client-preferences');
  })

});
