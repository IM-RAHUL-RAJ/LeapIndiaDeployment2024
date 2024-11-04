import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientPreferences } from 'src/app/models/Client/ClientPreferences';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { ClientPreferencesService } from 'src/app/services/Client/client-preferences.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';

@Component({
  selector: 'app-client-preferences',
  templateUrl: './client-preferences.component.html',
  styleUrls: ['./client-preferences.component.css']
})
export class ClientPreferencesComponent {

  clientProfileData!: ClientProfile
  clientPreferencesData!: any
  isClientFormFilled: boolean = false
  acceptAdvisor: boolean = false
  //activeRoute: any ;

  snackBarConfig = new MatSnackBarConfig();

  toleranceLevel = [
    { value: 1, name: "Very Low" },
    { value: 2, name: "Low" },
    { value: 3, name: "Moderate" },
    { value: 4, name: "High" },
    { value: 5, name: "Very High" },
  ]

  purposes = [
    { value: "Retirement", name: "Retirement" },
    { value: "Major Expense", name: "Major Expense" },
    { value: "Education", name: "Education" },
    { value: "Gift", name: "Gift" },
  ]

  incomes = [
    { value: "LIG", name: "Low Income Group (6L and Below)" },
    { value: "MIG", name: "Middle Income Group (Between 6L and 12L)" },
    { value: "HIG", name: "High Income Group (Between 12L and 18L)" },
    { value: "VHIG", name: "Very High Income Group (Above 18L)" },
  ]

  lengths = [
    { value: "Short", name: "Short Term ( <5years )" },
    { value: "Medium", name: "Medium Term ( 5-10 years )" },
    { value: "Long", name: "Long Term ( 10-20 years )" },
  ]

  percentages = [
    { value: "Tier4", name: "Less than 25%" },
    { value: "Tier3", name: "Between 26% and 50%" },
    { value: "Tier2", name: "Between 51% and 75%" },
    { value: "Tier1", name: "More than 75%" },
  ]

  preferences: FormGroup = new FormGroup({
    investmentPurpose: new FormControl('', Validators.required),
    incomeCategory: new FormControl('', Validators.required),
    lengthOfInvestment: new FormControl('', Validators.required),
    percentageOfSpend: new FormControl('', Validators.required),
    riskTolerance: new FormControl(1),
    acceptAdvisor: new FormControl(false)
  })

  constructor(private clientPreferencesService: ClientPreferencesService, private clientProfileService: ClientProfileService,
    private router: Router, private snackBar: MatSnackBar) {
        //this.activeRoute = this.activatedRoute.snapshot
    }

  ngOnInit() {
    this.snackBarConfig.duration = 3000;
    this.snackBarConfig.panelClass = ['red-snackbar'];
   // console.log("Component:", this.activeRoute.component.name)
    this.clientProfileService.getClientProfile().subscribe({
      next: (profile) => {
        console.log('Logged In Client Profile Data: ', profile);
        if(profile && profile.client?.clientId){
          this.clientProfileData = profile
          this.getPreferences(profile.client.clientId);
        }
      },
      error: (e) => {
        console.log('Registering Client Preferences error: ', e)
        this.snackBar.open(e, '', this.snackBarConfig)
      }
    })
  }

  setPreferencesFormData(clientPreferences: ClientPreferences) {
    this.acceptAdvisor = (clientPreferences.acceptAdvisor == "true") ? true : false
    this.preferences.patchValue({
      investmentPurpose: clientPreferences.investmentPurpose,
      incomeCategory: clientPreferences.incomeCategory,
      lengthOfInvestment: clientPreferences.lengthOfInvestment,
      percentageOfSpend: clientPreferences.percentageOfSpend,
      riskTolerance: clientPreferences.riskTolerance,
      acceptAdvisor: this.acceptAdvisor,
    })
  }

  savePreferences() {
    let obj = this.preferences.getRawValue()
    obj.clientId = this.clientProfileData?.client?.clientId
    this.isClientFormFilled ? this.updatePreferences(obj) : this.setPreferences(obj)
  }

  getPreferences(clientId: string){
    this.clientPreferencesService.getClientPreferences(clientId).subscribe({
      next: (data: any) => {
        if (data!==null && Object.keys(data).length != 0) {
          this.isClientFormFilled = true
          this.clientPreferencesData = data
          this.setPreferencesFormData(this.clientPreferencesData)
        }
        else {
          this.snackBarConfig.panelClass = ['red-snackbar'];
          this.clientPreferencesData = null
          this.snackBar.open("Unexpected error retrieving client preferences", '', this.snackBarConfig)
        }
      },
      error: (e: any) => {
        this.snackBarConfig.panelClass = ['red-snackbar'];
        console.log('Getting client preferences error: ', e)
        this.snackBar.open(e, '', this.snackBarConfig)
      }
    })
  }

  updatePreferences(obj: any) {
    this.snackBarConfig.duration = 3000;
    this.snackBarConfig.panelClass = ['form-submit-snackbar'];

    this.clientPreferencesService.updateClientPreferences(obj).subscribe({
      next: (data: any) => {
        console.log('New Preferences Submitted Data: ', data)
        if (data && data.clientId === this.clientProfileData?.client?.clientId) {
          this.snackBarConfig.panelClass = ['form-submit-snackbar'];
          this.snackBar.open('Preferences updated successfully', '', this.snackBarConfig)
          this.redirectToHome()
        }
        else {
          this.snackBarConfig.panelClass = ['red-snackbar'];
          this.snackBar.open('Client preferences couldn\'t be updated! Unexpected error at service!', '', this.snackBarConfig)
        }
      },
      error: (err) => {
        this.snackBarConfig.panelClass = ['red-snackbar'];
        this.snackBar.open(err, '', this.snackBarConfig)
      }
    })
  }

  setPreferences(obj: any) {

    this.snackBarConfig.duration = 3000;
    this.snackBarConfig.panelClass = ['form-submit-snackbar'];

    this.clientPreferencesService.setClientPreferences(obj).subscribe({
      next: (data: any) => {
        console.log('New Preferences Submitted Data: ', data)
        if (data && data.clientId === this.clientProfileData?.client?.clientId) {
          this.snackBarConfig.panelClass = ['form-submit-snackbar'];
          this.snackBar.open('Preferences saved successfully', '', this.snackBarConfig)
          this.isClientFormFilled = true
          this.clientPreferencesData = data
          this.redirectToHome()
        }
        else {
          this.snackBarConfig.panelClass = ['red-snackbar'];
          this.snackBar.open('Client preferences couldn\'t be saved! Unexpected error at service!', '', this.snackBarConfig)
        }

      },
      error: (err) => {
        console.log(err)
        this.snackBarConfig.panelClass = ['red-snackbar'];
        this.snackBar.open(err, '', this.snackBarConfig)
      }
    })
  }

  redirectToHome() {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['/home']).then(() => {
        console.log(`After navigation I am on:${this.router.url}`)
      })
    })
  }
}
