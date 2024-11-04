import { Component, inject, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ColDef, GridOptions, SideBarDef } from 'ag-grid-community';
import { ClientPortfolio } from 'src/app/models/Client/ClientPortfolio';
import { ClientProfile } from 'src/app/models/Client/ClientProfile';
import { Trade } from 'src/app/models/Trade/trade';
import { ClientProfileService } from 'src/app/services/Client/client-profile.service';
import { TradeHistoryService } from 'src/app/services/Trade/trade-history.service';

@Component({
  selector: 'app-trading-history',
  templateUrl: './trading-history.component.html',
  styleUrls: ['./trading-history.component.css']
})
export class TradingHistoryComponent  implements OnInit{
  clientId: string | undefined;

  clientProfileData!: ClientProfile | null; //Client Profile data that is set with ClientProfileService
  clientPortfolioData!: ClientPortfolio | null;
  private _snackBar = inject(MatSnackBar);

  public columnDefs: ColDef[] = [{ 
      headerName: "Instrument ID", 
      field: "instrumentId"
    },{ 
      headerName: "Quantity", 
      field: "quantity",
    },{ 
      headerName: "Execution Price", 
      field: "executionPrice",
    },{ 
      headerName: "Executed At", 
      field: "executedAt",
      valueFormatter: params => {
        if (Array.isArray(params.value)) {
          const dateParts = params.value;
          const date = new Date(
            dateParts[0],   // year
            dateParts[1] - 1, // month (0-based)
            dateParts[2],   // day
            dateParts[3],   // hour
            dateParts[4],   // minute
            dateParts[5]    // second
          );
          const options: Intl.DateTimeFormatOptions = { 
            year: 'numeric', 
            month: '2-digit', 
            day: '2-digit', 
            hour: '2-digit', 
            minute: '2-digit', 
            hour12: true 
        };
          return date.toLocaleString('en-US', options);
        }
        return '';
      }
    },{ 
      headerName: "Direction", 
      field: "direction",
    },{ 
      headerName: "Cash Value", 
      field: "cashValue",
  }]

  public tradeHistoryData: Trade[] = [];


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
      },{
          id: 'filters',
          labelDefault: 'Filters',
          labelKey: 'filters',
          iconKey: 'filter',
          toolPanel: 'agFiltersToolPanel',
    }],    
    position: 'left',
  }

  constructor(
    private tradeHistoryService: TradeHistoryService,
    private clientProfileService: ClientProfileService,
  ) {}

  ngOnInit(): void {
    
    this.clientProfileService.getClientProfile().subscribe(profile => {
      this.clientProfileData = profile;
      console.log('client ID: ',this.clientProfileData?.client?.clientId);
      this.clientProfileData?.client?.clientId !== undefined ? this.clientId = this.clientProfileData?.client?.clientId : console.error('Client ID is of type undefined');
      this.loadTrades();
    })
  }

  loadTrades() {
    this.clientId !== undefined ? this.tradeHistoryService.getTrades(this.clientId)
      .subscribe({
        next: (data: any) => {
          if(data!==null && Object.keys(data).length != 0){
            this.tradeHistoryData = data.trades;
          }
          else{
            this._snackBar.open("No trade history data found for the client", '', {
              duration: 3000,
              panelClass: ['red-snackbar']
            })
          }
        },
        error: (e) => {
          console.log('Error in loading Trade History: ',e);
          this._snackBar.open(e, '', {
            duration: 3000,
            panelClass: ['red-snackbar']
          })
        }
      }) : console.error('Client ID is undefined.')
  }

}
