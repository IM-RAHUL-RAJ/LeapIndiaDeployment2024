import { ComponentFixture, fakeAsync, flush, inject, TestBed, tick } from '@angular/core/testing';

import { ClientPreferencesComponent } from './client-preferences.component';
import { of } from 'rxjs';
import { ClientPreferencesService } from 'src/app/services/Client/client-preferences.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';

import { MaterialModule } from 'src/app/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimations } from '@angular/platform-browser/animations';

const testClientPreferences: any = 
  {
    "investmentPurpose": "Education",
    "incomeCategory": "HIG",
    "lengthOfInvestment": "Short",
    "percentageOfSpend": "Tier4",
    "riskTolerance": 2,
    "acceptAdvisor": false,
    "clientId": "1654658069",
    "id": "15b4"
  }

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


let clientPreferencesMockService: any = jasmine.createSpyObj('ClientPreferencesService', ['getClientPreferences', 'updateClientPreferences', "setClientPreferences"]);
clientPreferencesMockService.getClientPreferences.and.returnValue(of(testClientPreferences));

let clientProfileMockService: any = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
clientProfileMockService.getClientProfile.and.returnValue(of(testClientProfile));

describe('ClientPreferencesComponent', () => {
  let component: ClientPreferencesComponent;
  let fixture: ComponentFixture<ClientPreferencesComponent>;
  let clientPreferencesService: jasmine.SpyObj<ClientPreferencesService>;
  let clientProfileService: jasmine.SpyObj<ClientProfileService>;

  beforeEach(async () => {
    const clientPreferencesServiceSpy = jasmine.createSpyObj('ClientPreferencesService', ['setClientPreferences', 'updateClientPreferences', 'getClientPreferences']);
    const clientProfileServiceSpy = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);

    await TestBed.configureTestingModule({
      declarations: [ 
        ClientPreferencesComponent,
      ],
      imports: [
        MaterialModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: ClientPreferencesService, useValue: clientPreferencesServiceSpy },
        { provide: ClientProfileService, useValue: clientProfileServiceSpy },
        provideAnimations() 
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientPreferencesComponent);
    component = fixture.componentInstance;

    clientPreferencesService = TestBed.inject(ClientPreferencesService) as jasmine.SpyObj<ClientPreferencesService>;
    clientProfileService = TestBed.inject(ClientProfileService) as jasmine.SpyObj<ClientProfileService>;

    clientProfileService.getClientProfile.and.returnValue(of(testClientProfile));
    clientPreferencesService.getClientPreferences.and.returnValue(of(testClientPreferences));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit form successfully', inject([ClientPreferencesService], 
    fakeAsync((service: ClientPreferencesService) => {
      {
        component.preferences.setValue({
          investmentPurpose: 'Retirement',
          incomeCategory: '',
          lengthOfInvestment: 'Medium',
          percentageOfSpend: 'Tier2',
          riskTolerance: 3,
          acceptAdvisor: true
        });
        let obj = component.preferences.getRawValue()
        obj.clientId = component.clientProfileData?.client?.clientId
    
        component.isClientFormFilled = false
    
        clientPreferencesService.setClientPreferences.and.returnValue(of(testClientProfile));
        
        component.savePreferences();
        tick();
    
        expect(service.setClientPreferences).toHaveBeenCalledWith(obj);
        flush();
      }
    })))


  it('validate invalid data in the form', () =>{
    spyOnProperty(component.preferences, 'valid').and.returnValue(false);
    expect(component.preferences.valid).toBe(false)
    fixture.detectChanges()
    let addCommentBtn = fixture.debugElement.nativeElement.querySelector('.submitButton');
    expect(addCommentBtn.disabled).toBeTruthy();
  })

  it('should update data to service', inject([ClientPreferencesService], 
    fakeAsync((service: ClientPreferencesService) => {
    component.preferences.setValue({
      investmentPurpose: 'Retirement',
      incomeCategory: '',
      lengthOfInvestment: 'Medium',
      percentageOfSpend: 'Tier2',
      riskTolerance: 3,
      acceptAdvisor: true
    });

    let obj = component.preferences.getRawValue()
    obj.clientId = component.clientProfileData?.client?.clientId

    component.isClientFormFilled = true

    clientPreferencesService.updateClientPreferences.and.returnValue(of(testClientProfile));
    
    component.savePreferences();
    tick();
    expect(service.updateClientPreferences).toHaveBeenCalledWith(obj);
    flush();
  })))
  
});
