import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Price } from 'src/app/models/Trade/price';
import { TradingFormComponent } from '../trading-form/trading-form.component';
import { PriceService } from 'src/app/services/Trade/price.service';

@Component({
  selector: 'app-buy',
  templateUrl: './buy.component.html',
  styleUrls: ['./buy.component.css']
})
export class BuyComponent {
  prices: Price[] = [];
  price?: Price;
  instrumentId?: string;
  instrumentIdPortfolio?: string;


  constructor(
    private priceService: PriceService,
    private _dialog: MatDialog
  ) {}

   ngOnInit(): void {
    this.loadAllPrices();
  }

  loadAllPrices() {
    //Get prices from local storage
    this.priceService.getLivePrices()
      .subscribe(data => this.prices = data);
  }


  params: any;
  agInit(params: any): void {
    this.params = params;
    this.instrumentId = (this.params.data.instrument)? this.params.data.instrument.instrumentId : this.params.data.instrumentId;
  }

  onClickBuy() {
    this.price =  this.prices.filter((price: Price) => price.instrument.instrumentId === this.instrumentId)[0];
    console.log('Buy', this.price.instrument);
    
    const tradeFormData = {
      askPrice: this.price.askPrice,
      bidPrice: this.price.bidPrice,
      priceTimeStamp: this.price.priceTimestamp,
      direction: 'B',
      instrument: this.price.instrument
    }

    this._dialog.open(TradingFormComponent, {
      data: tradeFormData,
      width: '50vw',
    });
  }
}
