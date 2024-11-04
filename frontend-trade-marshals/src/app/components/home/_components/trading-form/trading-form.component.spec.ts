import { ComponentFixture, TestBed } from '@angular/core/testing';
 
import { TradingFormComponent } from './trading-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TradeService } from 'src/app/services/Trade/trade.service';
import { TradeHistoryService } from 'src/app/services/Trade/trade-history.service';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MaterialModule } from 'src/app/material.module';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
 
 
describe('TradingFormComponent', () => {
  let component: TradingFormComponent;
  let fixture: ComponentFixture<TradingFormComponent>;
 
  let tradeMockService: any = jasmine.createSpyObj('TradeService', ['getClientProfile']);
 
  let tradeHistoryMockService: any = jasmine.createSpyObj('TradeHistoryService', ['getClientProfile']);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TradingFormComponent,
      ],
      imports: [
        HttpClientTestingModule,
        MatDialogModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatSelectModule,
        FormsModule
      ],
      providers: [
        {provide: TradeService, useValue: tradeMockService},
        {provide: TradeHistoryService, useValue: tradeHistoryMockService},
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    })
    .overrideTemplate(TradingFormComponent, '')
      .compileComponents();
 
    fixture = TestBed.createComponent(TradingFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
 
 
  });
 
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});