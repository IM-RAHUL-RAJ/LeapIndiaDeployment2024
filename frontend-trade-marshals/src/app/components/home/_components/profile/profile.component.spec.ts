import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileComponent } from './profile.component';
import { of } from 'rxjs';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { MaterialModule } from 'src/app/material.module';

const testClientProfile: any = 
{
  "client": 
  {
    "id":"688f",
    "email":"rishi@gmail.com",
    "clientId":"1212794226",
    "password":"Marsh2024",
    "name":"Rishiyanth",
    "dateOfBirth":"11/04/2002",
    "country":"India",
    "identification":[
      {
        "type":"Aadhar",
        "value":"123412341234"
      }
    ],
    "isAdmin":false
  },
  "token":1212670770
}

let clientProfileMockService: any = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
clientProfileMockService.getClientProfile.and.returnValue(of(testClientProfile));

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProfileComponent ],
      imports: [
        MaterialModule
      ],
      providers: [
        {provide: ClientProfileService, useValue: clientProfileMockService}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
