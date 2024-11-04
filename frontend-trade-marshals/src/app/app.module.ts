import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MaterialModule } from './material.module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { HomeComponent } from './components/home/home.component';
import { PortfolioComponent } from './components/home/_components/portfolio/portfolio.component';
import { ReportActivityComponent } from './components/home/_components/report-activity/report-activity.component';
import { TradingHistoryComponent } from './components/home/_components/trading-history/trading-history.component';
import { ProfileComponent } from './components/home/_components/profile/profile.component';
import { ClientPreferencesComponent } from './components/home/_components/client-preferences/client-preferences.component';
import { TradingFormComponent } from './components/home/_components/trading-form/trading-form.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AgGridModule } from 'ag-grid-angular';
import { ModuleRegistry } from '@ag-grid-community/core';
import { SideBarModule } from '@ag-grid-enterprise/side-bar';
import { PriceListComponent } from './components/home/_components/price-list/price-list.component';
import { HttpClientModule } from '@angular/common/http';
import { BuyComponent } from './components/home/_components/buy/buy.component';
import { SellComponent } from './components/home/_components/sell/sell.component';
import { DatePipe } from '@angular/common';
import { RoboAdvisorComponent } from './components/home/_components/robo-advisor/robo-advisor.component';

ModuleRegistry.registerModules([SideBarModule]);


@NgModule({
  declarations: [
    AppComponent,
    LandingPageComponent,
    HomeComponent,
    PortfolioComponent,
    ReportActivityComponent,
    TradingHistoryComponent,
    ProfileComponent,
    ClientPreferencesComponent,
    TradingFormComponent,
    RegistrationComponent,
    PriceListComponent,
    TradingHistoryComponent,
    BuyComponent,
    SellComponent,
    RoboAdvisorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    AgGridModule,
    HttpClientModule,
    FormsModule,
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
