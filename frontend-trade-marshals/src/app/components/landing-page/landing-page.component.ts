import { Component, ElementRef, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { LoginService } from 'src/app/services/Client/login.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit {

  //For Login Form - Popup
  @ViewChild('error') error!: ElementRef;
  @ViewChild('loginFormTemplate') loginFormTemplate!: TemplateRef<any>;

  //Form group for Login Credentials
  loginCredentials: FormGroup = new FormGroup({
    email: new FormControl('',Validators.required),
    password: new FormControl('',Validators.required)
  })

  clientProfile!: ClientProfile  //Logging in Client Data fetched from LoginService

  constructor(private dialog: MatDialog, private snackBar: MatSnackBar, private datePipe: DatePipe,
    private loginService: LoginService, private clientProfileService: ClientProfileService, private route: Router) { }

  ngOnInit() { }

  //To Open Popup Form
  openDialog(templateName: string) {
    let dialog: any;
    if (templateName === 'loginFormTemplate')
      dialog = this.dialog.open(this.loginFormTemplate, { width: '50%', height: '62%' });
    else
      console.log('Template doesnt exist');
  }
  //To Close Popup Form
  closeDialog() {
    this.dialog.closeAll()
  }

  //On Submitting Login Form
  onSubmitLoginForm() {
    console.log('Login Form Submitted Details: ', this.loginCredentials.value)

    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = ['form-submit-snackbar'];

    const errorSnackBarConfig = new MatSnackBarConfig();
    errorSnackBarConfig.duration = 3000;
    errorSnackBarConfig.panelClass = ['red-snackbar'];
    
    const email = this.loginCredentials.value.email
    const pwd = this.loginCredentials.value.password

    this.loginService.loginClient(email,pwd).subscribe({
      next: (data) => {
        if(data !== null){
          let formattedDate = this.datePipe.transform(data?.client?.dateOfBirth, 'dd/MM/yyyy');
          if(data.client !== null){
            data.client.dateOfBirth = String(formattedDate)
          }
          this.clientProfile = data
          this.clientProfileService.setClientProfile(this.clientProfile)
          this.snackBar.open('User has successfully logged in!', '', snackBarConfig)
          this.closeDialog()
          this.redirectToHome()
        }else{
          this.snackBar.open("Unexpected error in retrieving new client details", '', errorSnackBarConfig)
        }
      },
      error: (e) => { //Error in hitting fmts client data
        console.log(e)
        this.snackBar.open(e, '', errorSnackBarConfig)
        this.loginCredentials.reset()
      }
    })
  }

  //After succesful login
  redirectToHome() {
    this.route.navigateByUrl('/home')
  }

  //On Clicking Registration
  redirectToRegistration() {
    this.route.navigateByUrl('/register')
  }

}
