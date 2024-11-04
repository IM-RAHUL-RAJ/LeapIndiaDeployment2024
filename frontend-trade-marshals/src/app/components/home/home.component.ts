import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ClientPreferences } from 'src/app/models/Client/ClientPreferences';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { ClientPreferencesService } from 'src/app/services/Client/client-preferences.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { RoboAdvisorComponent } from './_components/robo-advisor/robo-advisor.component';
import { PriceService } from 'src/app/services/Trade/price.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  clientProfileData!: ClientProfile //Client Profile data that is set with ClientProfileService
  clientPreferencesData!: ClientPreferences | null //Client Preferences data 
  isSideMenuExpanded: boolean = false; //Side Menu
  isHomeContent: boolean = true; //Home Content displayed

  constructor(private clientProfileService: ClientProfileService, private clientPreferencesService: ClientPreferencesService,
    private priceService: PriceService, private router: Router, public dialog: MatDialog) { }

  ngOnInit() {
    this.isSideMenuExpanded = false
    console.log('Router URL: ', this.router.url)
    if (this.router.url === '/home')
      this.isHomeContent = true //Set Home Page Content
    else
      this.isHomeContent = false

    //Getting Client Profile data from service
    this.clientProfileService.getClientProfile().subscribe(profile => {
      this.clientProfileData = profile
      console.log('Logged In Client Profile Data: ', this.clientProfileData);
      if (this.clientProfileData && this.clientProfileData.client?.clientId)
        this.clientPreferencesService.getClientPreferences(this.clientProfileData.client.clientId).subscribe({
          next: (data: ClientPreferences) => {
            console.log(data)
            if (data) {
              this.clientPreferencesData = data
            }
            else {
              this.clientPreferencesData = null
            }
          }
        })
    })
  }

  //To go to Home
  redirectToHome() {
    this.isHomeContent = true
    this.router.navigateByUrl('/home')
  }

  //To go to Profile Component
  redirectToProfile() {
    this.isHomeContent = false
    this.router.navigateByUrl('/home/profile')
  }

  //When client logs out - To go back to Landing
  logout() {
    this.priceService.removeLivePrices() //Erasing live prices data - from FMTS
    this.clientProfileService.removeClientProfile() //Erasing Client data
    this.router.navigateByUrl('/')
  }

  openDialog() {
    const dialogRef = this.dialog.open(RoboAdvisorComponent, {
      height: '65%',
      width: '80%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }
}

