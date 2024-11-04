import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TradingHistoryComponent } from './trading-history.component';
import { of } from 'rxjs';
import { TradeHistoryService } from 'src/app/services/Trade/trade-history.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { MaterialModule } from 'src/app/material.module';
import { AgGridAngular, AgGridModule } from 'ag-grid-angular';
import { DatePipe } from '@angular/common';

const testTrade = {
  "id": "16ef",
  "instrumentId": "C100",
  "quantity": 100,
  "executionPrice": 95.92,
  "direction": "B",
  "clientId": "920265077",
  "order": {
    "instrumentId": "C100",
    "quantity": 100,
    "targetPrice": 95.42,
    "direction": "B",
    "clientId": "920265077",
    "token": 920141621
  },
  "tradeId": "c9ue1ftfp5a-zpgldismbaj-zlhj1ya3ju",
  "cashValue": 9687.92
}

const testClientProfile: any =
{
  "client":
  {
    "id": "688f",
    "email": "rishi@gmail.com",
    "clientId": "1212794226",
    "password": "Marsh2024",
    "name": "Rishiyanth",
    "dateOfBirth": "11/04/2002",
    "country": "India",
    "identification": [
      {
        "type": "Aadhar",
        "value": "123412341234"
      }
    ],
    "isAdmin": false
  },
  "token": 1212670770
}

let tradeHistoryMockService: any = jasmine.createSpyObj('TradeHistoryService', ['getTrades']);
tradeHistoryMockService.getTrades.and.returnValue(of(testTrade));

let clientProfileMockService: any = jasmine.createSpyObj('ClientProfileService', ['getClientProfile']);
clientProfileMockService.getClientProfile.and.returnValue(of(testClientProfile));

describe('TradingHistoryComponent', () => {
  let component: TradingHistoryComponent;
  let fixture: ComponentFixture<TradingHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TradingHistoryComponent,
      ],
      imports: [
        MaterialModule,
      ],
      providers: [
        DatePipe,
        { provide: TradeHistoryService, useValue: tradeHistoryMockService },
        { provide: ClientProfileService, useValue: clientProfileMockService }
      ]
    })
    .overrideTemplate(TradingHistoryComponent, '')
    .compileComponents();

    fixture = TestBed.createComponent(TradingHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
