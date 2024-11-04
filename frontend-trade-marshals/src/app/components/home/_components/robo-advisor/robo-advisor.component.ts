import { Component } from '@angular/core';
import { Price } from 'src/app/models/Trade/price';
import { ColDef, SideBarDef } from 'ag-grid-community';
import { SellComponent } from '../sell/sell.component';
import { BuyComponent } from '../buy/buy.component';
import { RoboAdvisorService } from 'src/app/services/Trade/robo-advisor.service';
import { ClientPreferencesService } from 'src/app/services/Client/client-preferences.service';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { ClientPreferences } from 'src/app/models/Client/ClientPreferences';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Component({
  selector: 'app-robo-advisor',
  templateUrl: './robo-advisor.component.html',
  styleUrls: ['./robo-advisor.component.css']
})
export class RoboAdvisorComponent {

  isBuy: boolean = false;

  buyPrices: Price[] = [];
  sellPrices: Price[] = [];

  snackBarConfig = new MatSnackBarConfig();
    

  public buyColumnDefs: ColDef[] = [
    {
      headerName: "Instrument ID",
      field: "instrument.instrumentId",
    },
    {
      headerName: "Instrument Description",
      field: "instrument.instrumentDescription",
    },
    {
      headerName: "Category ID",
      field: "instrument.categoryId",
    },
    {
      headerName: "Bid Price",
      field: "bidPrice",
    },
    {
      headerName: "Buy",
      field: "buy",
      minWidth: 150,
      cellRenderer: BuyComponent,
    },

  ]

  public sellColumnDefs: ColDef[] = [
    {
      headerName: "Instrument ID",
      field: "instrument.instrumentId",
    },
    {
      headerName: "Instrument Description",
      field: "instrument.instrumentDescription",
    },
    {
      headerName: "Category ID",
      field: "instrument.categoryId",
    },
    {
      headerName: "Ask Price",
      field: "askPrice",
    },
    {
      headerName: "Sell",
      field: "sell",
      minWidth: 150,
      cellRenderer: SellComponent
    },
  ]

  public columnDefs: ColDef[] = [{
    headerName: "Ask Price",
    field: "askPrice",
  }, {
    headerName: "Bid Price",
    field: "bidPrice",
  }, {
    headerName: "Price Timestamp",
    field: "priceTimestamp",
  }, {
    headerName: "Instrument ID",
    field: "instrument.instrumentId",
  }, {
    headerName: "External ID Type",
    field: "instrument.externalIdType",
  }, {
    headerName: "External ID",
    field: "instrument.externalId",
  }, {
    headerName: "Category ID",
    field: "instrument.categoryId",
  }, {
    headerName: "Instrument Description",
    field: "instrument.instrumentDescription",
  }, {
    headerName: "Max Quantity",
    field: "instrument.maxQuantity",
  }, {
    headerName: "Min Quantity",
    field: "instrument.minQuantity",
  }, {
    headerName: "Min Quantity",
    field: "instrument.minQuantity",
  },
  {
    headerName: "Buy",
    field: "buy",
    cellRenderer: BuyComponent
  }, {
    headerName: "Sell",
    field: "sell",
    cellRenderer: SellComponent
  },
  ]

  public defaultColDef: ColDef = {
    flex: 1,
    resizable: true,
    sortable: true,
    filter: true,
  }

  public sidebar: SideBarDef | null = {
    toolPanels: [
      {
        id: 'columns',
        labelDefault: 'Columns',
        labelKey: 'columns',
        iconKey: 'columns',
        toolPanel: 'agColumnsToolPanel',
      }, {
        id: 'filters',
        labelDefault: 'Filters',
        labelKey: 'filters',
        iconKey: 'filter',
        toolPanel: 'agFiltersToolPanel',
      }],
    position: 'left',
  }

  constructor(
    private roboAdvisorService: RoboAdvisorService,
    private preferencesService: ClientPreferencesService,
    private profileService: ClientProfileService,
    private snackBar: MatSnackBar
  ) {
    this.snackBarConfig.duration = 2000;
    this.snackBarConfig.panelClass = ['red-snackbar'];
  }

  ngOnInit(): void {
    this.loadAllPrices();
  }

  loadAllPrices() {
    this.profileService.getClientProfile().subscribe((profile) => {
      if(profile!==null || Object.keys(profile).length != 0){
        let clientId = profile?.client?.clientId
        if (clientId){
          this.preferencesService.getClientPreferences(clientId).subscribe((preferences) => {
            this.retrieveAllTopBuys(preferences)
            this.retrieveAllTopSells(preferences)
          });
        }
      }
    })
  }

  retrieveAllTopBuys(preferences: ClientPreferences) {
    
    this.roboAdvisorService.getTopBuyTrades(preferences)
      .subscribe({
        next: (data) => {
          if (data !== null && Object.keys(data).length != 0) {
            this.buyPrices = data;
          } else {
            this.snackBar.open("Unexpected error in retrieving robo advisor buy recommendations", '', this.snackBarConfig)
          }
        },
        error: (e) => {
          console.log(e)
          this.snackBar.open(e, '', this.snackBarConfig)
        }
      });
  }

  retrieveAllTopSells(preferences: ClientPreferences) {
    this.roboAdvisorService.getTopSellTrades(preferences)
      .subscribe({
        next: (data) => {
          if (data !== null  && Object.keys(data).length != 0) {
            this.sellPrices = data;
          } else {
           this.snackBar.open("Client has no holdings for robo advisor to give sell recommendations", '', this.snackBarConfig)
          }
        },
        error: (e) => {
          console.log(e)
          this.snackBar.open(e, '', this.snackBarConfig)
        }
      });
  }
}
