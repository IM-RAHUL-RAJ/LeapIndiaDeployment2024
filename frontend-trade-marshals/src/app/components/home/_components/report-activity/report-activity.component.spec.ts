import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportActivityComponent } from './report-activity.component';
import { MaterialModule } from 'src/app/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { of } from 'rxjs';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { PriceService } from 'src/app/services/Trade/price.service';
import { ClientActivityReportService } from 'src/app/services/Client/client-activity-report.service';

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

describe('ReportActivityComponent', () => {
  let component: ReportActivityComponent;
  let fixture: ComponentFixture<ReportActivityComponent>;

  let clientProfileMockService:any;
  let mockGetClientProfileSpy:any;

  let priceMockService:any
  let clientActivityReportMockService:any

  beforeEach(async () => {

    clientProfileMockService = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
    mockGetClientProfileSpy = clientProfileMockService.getClientProfile.and.returnValue(of(testClientProfile));

    priceMockService = jasmine.createSpyObj('PriceService', ['getLivePrices']);

    clientActivityReportMockService = jasmine.createSpyObj('ClientActivityReportService',
             ['getClientProfitLossReport','getClientHoldingsReport','getClientTradeReport']);

    await TestBed.configureTestingModule({
      declarations: [ ReportActivityComponent ],
      imports: [
        MaterialModule,
        ReactiveFormsModule
      ],
      providers: [
        provideAnimations(),
        { provide: ClientProfileService, useValue: clientProfileMockService },
        { provide: PriceService, useValue: priceMockService },
        { provide: ClientActivityReportService, useValue: clientActivityReportMockService },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
});
