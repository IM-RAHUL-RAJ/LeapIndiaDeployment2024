import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuyComponent } from './buy.component';
import { PriceService } from 'src/app/services/Trade/price.service';
import { MaterialModule } from 'src/app/material.module';
import { of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { TradingFormComponent } from '../trading-form/trading-form.component';

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
  }
]

describe('BuyComponent', () => {
  let component: BuyComponent;
  let fixture: ComponentFixture<BuyComponent>;

  let priceMockService:any; 
  let getLivePricesSpy:any; 
  
  let dialogMock: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    dialogMock = jasmine.createSpyObj('MatDialog', ['open']);
    priceMockService = jasmine.createSpyObj('PriceService', ['getLivePrices'])
    getLivePricesSpy = priceMockService.getLivePrices.and.returnValue(of(mockPrices))

    await TestBed.configureTestingModule({
      declarations: [BuyComponent],
      imports: [MaterialModule],
      providers: [
        { provide: PriceService, useValue: priceMockService },
        { provide: MatDialog, useValue: dialogMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(BuyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadAllPrices on initialization', () => {
    spyOn(component, 'loadAllPrices').and.callThrough();
    component.ngOnInit();
    expect(component.loadAllPrices).toHaveBeenCalled();
  });

  it('should load prices from the service and assign to the component', () => {
    component.loadAllPrices();
    expect(getLivePricesSpy).toHaveBeenCalled()
    expect(component.prices).toEqual(mockPrices);
  });

  it('should open a dialog with the correct data', () => {
    const price = mockPrices[0];
    const expectedData = {
      askPrice: price.askPrice,
      bidPrice: price.bidPrice,
      priceTimeStamp: price.priceTimestamp,
      direction: 'B',
      instrument: price.instrument
    };
    //Fixing component's instrumentId before checking
    component.instrumentId = price.instrument.instrumentId;
    fixture.detectChanges();
    component.onClickBuy();
    expect(dialogMock.open).toHaveBeenCalledWith(TradingFormComponent, {
      width: '50vw',
      data: expectedData
    });
  });

  it('should initialize params correctly', () => {
    const instrument = {
      "instrumentId": "N123456",
      "externalIdType": "CUSIP",
      "externalId": "46625H100",
      "categoryId": "STOCK",
      "instrumentDescription": "JPMorgan Chase & Co. Capital Stock",
      "maxQuantity": 1000,
      "minQuantity": 1
    }
    const params = { data: instrument };
    component.agInit(params);
    expect(component.params).toEqual(params);
  });
});
