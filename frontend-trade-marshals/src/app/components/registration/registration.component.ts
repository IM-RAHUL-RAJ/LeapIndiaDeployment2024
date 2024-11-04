import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Client } from 'src/app/models/Client/Client';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { RegisterService } from 'src/app/services/Client/register.service';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { MatStepper } from '@angular/material/stepper';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  today = new Date()
  errorMessage: string = '' //For Form Validation

  //Validate password strength
  passwordStrengthValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const value = control.value;
      const hasUppercase = /[A-Z]/.test(value);
      const hasLowercase = /[a-z]/.test(value);
      const hasNumber = /\d/.test(value);
      const isValid = hasUppercase && hasLowercase && hasNumber;
      isValid ? this.errorMessage = "" : this.errorMessage = 'Password must contain at least one uppercase letter, one lowercase letter, and one number.';
      return isValid ? null : { passwordStrength: 'Password must contain at least one uppercase letter, one lowercase letter, and one number.' }
    };
  }

  //Validating if password matches
  samePasswordValidator: ValidatorFn = (control: AbstractControl,): ValidationErrors | null => {
    const password = control.get('password');
    const retypePassword = control.get('retypePassword');

    return password && retypePassword && password.value !== retypePassword.value
      ? { identityRevealed: true }
      : null;
  };

  //Validating Govt Proof
  validateGovernmentProof: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    let idType = control.get('type')?.value;
    let idValue = control.get('value')?.value;
    
    // Aadhar validation: must be exactly 12 digits
    if (idType === "Aadhar") {
      if (!/^\d{12}$/.test(idValue)) {
        return { validID: true };
      }
      return null;
    }
    // PAN validation: must be 5 uppercase letters, 4 digits, 1 uppercase letter
    else if (idType === "PAN") {
      if (!/^[A-Z]{5}\d{4}[A-Z]$/.test(idValue)) {
        return { validID: true };
      }
      return null;
    }
    // SSN validation: must be exactly 9 digits
    else if (idType === "SSN") {
      if (!/^\d{9}$/.test(idValue)) {
        return { validID: true };
      }
      return null;
    }
    // If none of the conditions are met, return an error for id not matched
    return { validID: false };
  };

  //3 FormGroups for the Registration Step Up Forms
  signupForm: FormGroup = new FormGroup({
    email: new FormControl('', Validators.required),
    password: new FormControl('', [Validators.required, Validators.minLength(8), this.passwordStrengthValidator()]),
    retypePassword: new FormControl('', Validators.required)
  }, { validators: this.samePasswordValidator })

  personalDetails: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    doB: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
  })

  identificationDetails: FormGroup = new FormGroup({
    type: new FormControl('', Validators.required),
    value: new FormControl('', Validators.required),
  }, { validators: this.validateGovernmentProof })

  //List of countries and theirs Govt IDs
  countries = [
    { value: "India", name: "India" },
    { value: "USA", name: "USA" },
  ]
  indianIds = [
    { value: "Aadhar", name: "Aadhar Card" },
    { value: "PAN", name: "Permanent Account Number (PAN)" },
  ]
  usaIds = [
    { value: "SSN", name: "Social Security Number (SSN)" },
  ]

  identificationTypes: any //To set the Govt ID Types for the third form

  clientData!: Client | null  //For registration / storing
  clientProfileData!: ClientProfile  //Client Profile data that is set with ClientProfileService

  constructor(private route: Router, private snackBar: MatSnackBar, private datePipe: DatePipe,
    private registerService: RegisterService, private clientProfileService: ClientProfileService) { }

  ngOnInit() {
    this.today.setFullYear(this.today.getFullYear() - 18, 11, 31);
    this.clientData = null
    this.signupForm.reset()
    this.personalDetails.reset()
    this.identificationDetails.reset()
  }

  //To go back to Landing Page
  redirectToLandingPage() {
    this.route.navigateByUrl('/')
  }
  //To go back to Client Preferences Page
  redirectToClientPreferencesPage() {
    this.route.navigateByUrl('/home/client-preferences')
  }

  //Logic to check if the email already exists - For Signup
  validateNewEmail(stepper: MatStepper) {
    console.log('Sign Up Form Submitted Details: ', this.signupForm.value)

    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = ['form-submit-snackbar'];

    const errorSnackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = ['red-snackbar'];

    this.registerService.getVerificationOfClientEmail(this.signupForm.value.email).subscribe(
      {
        next: (data: any) => { //If success
          let isVerified = data.isVerified
          console.log('Client data from Service: ', data)
          if (isVerified) { //Registering an existing Client 
            this.snackBar.open('User is already registered! Register with a different email', '', snackBarConfig)
            this.signupForm.reset()
            // Client is now successfully registered and clientId is retrieved - Going to next stop
          } else {
            stepper.next()
          }
        },
        error: (e) => { //Error in hitting register service
          console.log(e)
          this.snackBar.open(e, '', errorSnackBarConfig)
          this.signupForm.reset()
        }
      })
  }

  //Retrieves country entered by user
  get country() {
    return this.personalDetails.get('country')
  }

  //Set Govt ID after submitting personal details form
  setGovtIDType(stepper: MatStepper) {
    console.log(this.personalDetails.value)
    this.country?.value === 'India' ? this.identificationTypes = this.indianIds : this.identificationTypes = this.usaIds
    stepper.next() //Manually go to next step
  }

  // Submission of Registration Form after getting all details - Save and update our DB
  submitRegistrationForm() {
    console.log('Govt ID Details Submitted: ', this.identificationDetails.value)

    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = ['form-submit-snackbar'];

    const errorSnackBarConfig = new MatSnackBarConfig();
    errorSnackBarConfig.duration = 3000;
    errorSnackBarConfig.panelClass = ['red-snackbar'];

    let formattedDate = this.datePipe.transform(this.personalDetails.value?.doB, 'yyyy-MM-dd'); //Transform doB with pipe operator
    this.clientData = {
      'email': this.signupForm.value.email,
      'password': this.signupForm.value.password,
      'name': this.personalDetails.value.name,
      'dateOfBirth': String(formattedDate),
      'country': this.personalDetails.value.country,
      'identification': [{ 'type': this.identificationDetails.value.type, 'value': this.identificationDetails.value.value }],
    }
    console.log('Registered Data to be posted to the Service: ', this.clientData)
    //Saving Registered client with service
    this.registerService.saveClientDetails(this.clientData).subscribe({
      next: (data) => {
        //After successful client validation - Set Client Profile data
        if (data !== null) {
          let formattedDate = this.datePipe.transform(data?.client?.dateOfBirth, 'dd/MM/yyyy');
          if (data.client !== null) {
            data.client.dateOfBirth = String(formattedDate)
          }
          this.clientProfileData = data
          this.clientProfileService.setClientProfile(this.clientProfileData)
          //After setting profile redirect to Client Preferences Component
          this.redirectToClientPreferencesPage()
        } else {
          this.snackBar.open("Unexpected error in retrieving new client details", '', errorSnackBarConfig)
        }
      },
      error: (e) => {
        console.log('Registering Client error: ', e)
        this.snackBar.open(e, '', errorSnackBarConfig)
      }
    })
  }

}
