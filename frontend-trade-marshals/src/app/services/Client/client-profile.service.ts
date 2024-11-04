import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class ClientProfileService {

  // private clientProfileSubject = new BehaviorSubject<ClientProfile | null>(null);
  // clientProfile$ = this.clientProfileSubject.asObservable();

  constructor() { }

  //Getting Client Profile Details - Called from every other component after successful login/register
  getClientProfile(): Observable<ClientProfile> {
    let profile = localStorage.getItem('clientDetails')!
    return of(JSON.parse(profile))
  }

  //Setting Client Profile Details - Called from Landing Page/Register component after successful login/register
  setClientProfile(profile:ClientProfile) {
    const hashedPassword = CryptoJS.SHA256(profile.client!.password).toString();
    let updatedClient = {...profile.client, password: hashedPassword }
    let updatedClientProfile = { ...profile, client: updatedClient}
    console.log(updatedClientProfile)
    localStorage.setItem('clientDetails', JSON.stringify(updatedClientProfile) )
  }

  //Removing Client Profile from local storage on Logout
  removeClientProfile(){
    localStorage.removeItem('clientDetails')
  }

}