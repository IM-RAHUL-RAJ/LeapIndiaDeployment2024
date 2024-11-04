import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioComponent } from './portfolio.component';
import { ClientPortfolioService } from 'src/app/services/Client/client-portfolio.service';
import { of } from 'rxjs';
import { AgGridModule } from 'ag-grid-angular';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { MaterialModule } from 'src/app/material.module';
import { PriceService } from 'src/app/services/Trade/price.service';

const testPortfolioData = {
  "clientId": "1654658069",
  "currBalance": 10000,
  "holdings": []
}

const testClientProfile: any =
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
  "token": 1654658069
}

describe('PortfolioComponent', () => {
  let component: PortfolioComponent;
  let fixture: ComponentFixture<PortfolioComponent>;
  
  let clientProfileMockService: any 
  let getProfileSpy:any 

  let clientPortfolioMockService: any 
  let getPortfolioSpy:any 

  let priceMockService: any

  beforeEach(async () => {

    clientProfileMockService = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
    getProfileSpy = clientProfileMockService.getClientProfile.and.returnValue(of(testClientProfile));

    clientPortfolioMockService = jasmine.createSpyObj('ClientPortfolioService', ['getClientPortfolio']);
    getPortfolioSpy =  clientPortfolioMockService.getClientPortfolio.and.returnValue(of(testPortfolioData));

    priceMockService = jasmine.createSpyObj('PriceService', ['getLivePrices']);

    await TestBed.configureTestingModule({
      declarations: [PortfolioComponent],
      imports: [
        AgGridModule,
        MaterialModule
      ],
      providers: [
        { provide: ClientPortfolioService, useValue: clientPortfolioMockService },
        { provide: ClientProfileService, useValue: clientProfileMockService },
        { provide: PriceService, useValue: priceMockService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PortfolioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
