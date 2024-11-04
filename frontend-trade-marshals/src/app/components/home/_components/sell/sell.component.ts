import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Price } from 'src/app/models/Trade/price';
import { TradingFormComponent } from '../trading-form/trading-form.component';
import { PriceService } from 'src/app/services/Trade/price.service';

@Component({
  selector: 'app-sell',
  templateUrl: './sell.component.html',
  styleUrls: ['./sell.component.css']
})
export class SellComponent implements OnInit {
  prices: Price[] = [];
  price?: Price;
  instrumentId?: String;

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

  onClickSell() {
    this.price =  this.prices.filter((price: Price) => price.instrument.instrumentId === this.instrumentId)[0];
    console.log('Sell', this.price.instrument);
    
    const tradeFormData = {
      askPrice: this.price.askPrice,
      bidPrice: this.price.bidPrice,
      priceTimeStamp: this.price.priceTimestamp,
      direction: 'S',
      instrument: this.price.instrument
    }

    this._dialog.open(TradingFormComponent, {
      data: tradeFormData,
      width: '50vw',
    });
  }
  
}
