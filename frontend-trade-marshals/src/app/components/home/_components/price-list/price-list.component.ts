import { Component, OnInit } from '@angular/core';
import { Price } from 'src/app/models/Trade/price';
import { PriceService } from 'src/app/services/Trade/price.service';
import { CellClickedEvent, ColDef, SideBarDef } from 'ag-grid-community';
import { SellComponent } from '../sell/sell.component';
import { BuyComponent } from '../buy/buy.component';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Component({
  selector: 'app-price-list',
  templateUrl: './price-list.component.html',
  styleUrls: ['./price-list.component.css']
})
export class PriceListComponent implements OnInit{
  prices: Price[] = [];
  instrumentIdSelected?: string;

  public columnDefs: ColDef[] = [{ 
    headerName: "Instrument Description", 
    field: "instrument.instrumentDescription",
    minWidth: 300,
  },{ 
    headerName: "Category ID", 
    field: "instrument.categoryId",
  },{ 
    headerName: "Ask Price", 
    field: "askPrice",
  },{ 
    headerName: "Bid Price", 
    field: "bidPrice",
  },{ 
    headerName: "Max Quantity", 
    field: "instrument.maxQuantity",
  },{ 
    headerName: "Min Quantity", 
    field: "instrument.minQuantity",
  },{ 
    headerName: "Buy", 
    field: "buy",
    cellRenderer: BuyComponent,
    cellRendererParams: {
      instrumentId: this.instrumentIdSelected
    }
  },
  { 
    headerName: "Sell", 
    field: "sell",
    cellRenderer: SellComponent,
    cellRendererParams: {
      instrumentId: this.instrumentIdSelected
    }
  }
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
    private priceService: PriceService, private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAllPrices();
  }

  loadAllPrices() {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 2000;
    snackBarConfig.panelClass = ['red-snackbar'];

    this.priceService.getPricesFromFMTS()
      .subscribe({
        next: (data: Price[]) => {
          if(data!==null && Object.keys(data).length != 0) {
            this.prices = data;
            this.priceService.setLivePrices(this.prices); //Set the prices
          }else{
            this.snackBar.open("Unexpected error in retrieving live instrument prices", '', snackBarConfig)
          }
        },
        error: (e) => {
          console.log(e)
          this.snackBar.open(e, '', snackBarConfig)
        }
      })
  }

  // onGridReady(params){
  //   this.gridApi = params.api;
  // }

  // onRowSelect(event) {
  //   const selectedRow = this.gridApi.getSelectedRow();
  //   console.log("On Click Button", event.data);
  // }

  // onSelectionChanged() {
    // const selectedRows =  this.gridApi?.getSelectedRows();
    // const instrumentId = selectedRows.length === 1 ? selectedRows[0].instrumentId : "";
    // console.log('Selected Row: ', instrumentId);
  // }
 

  cellClicked(event: CellClickedEvent) {
    if(event.node.data) {
        console.log("Selected Node: ", event.node.data);
        this.instrumentIdSelected = event.node.data.instrument.instrumentId;
    }
  }

}