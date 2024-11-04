import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriceListComponent } from './price-list.component';
import { of } from 'rxjs';
import { PriceService } from 'src/app/services/Trade/price.service';
import { AgGridModule } from 'ag-grid-angular';
import { MaterialModule } from 'src/app/material.module';
import { MatSnackBar } from '@angular/material/snack-bar';

const mockPrices = [
  {
    "askPrice": 104.75,
    "bidPrice": 104.25,
    "priceTimestamp": "21-AUG-19 10.00.01.042000000 AM GMT",
    "instrument": {
      "instrumentId": "N123456",
      "externalIdType": "CUSIP",
      "externalId": "46625H100",
      "categoryId": "STOCK",
      "instrumentDescription": "JPMorgan Chase & Co. Capital Stock",
      "maxQuantity": 1000,
      "minQuantity": 1
    }
  },
  {
    "askPrice": 312500,
    "bidPrice": 312000,
    "priceTimestamp": "21-AUG-19 05.00.00.040000000 AM -05:00",
    "instrument": {
      "instrumentId": "N123789",
      "externalIdType": "ISIN",
      "externalId": "US0846707026",
      "categoryId": "STOCK",
      "instrumentDescription": "Berkshire Hathaway Inc. Class A",
      "maxQuantity": 10,
      "minQuantity": 1
    }
  },
]

describe('PriceListComponent', () => {
  let component: PriceListComponent;
  let fixture: ComponentFixture<PriceListComponent>;

  let priceMockService:any
  let getLivePricesSpy:any

  let snackBar: any

  beforeEach(async () => {
    priceMockService = jasmine.createSpyObj('PriceService', ['getPricesFromFMTS','setLivePrices'])
    getLivePricesSpy = priceMockService.getPricesFromFMTS.and.returnValue(of(mockPrices))

    snackBar = jasmine.createSpyObj('MatSnackBar', ['open']); //Spying on MatSnackBar

    await TestBed.configureTestingModule({
      declarations: [PriceListComponent],
      imports: [AgGridModule, MaterialModule],
      providers: [
        {provide: PriceService, useValue: priceMockService},
        { provide: MatSnackBar, useValue: snackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PriceListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
