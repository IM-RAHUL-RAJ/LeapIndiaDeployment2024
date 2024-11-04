import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/home/_components/profile/profile.component';
import { ClientPreferencesComponent } from './components/home/_components/client-preferences/client-preferences.component';
import { PortfolioComponent } from './components/home/_components/portfolio/portfolio.component';
import { TradingHistoryComponent } from './components/home/_components/trading-history/trading-history.component';
import { ReportActivityComponent } from './components/home/_components/report-activity/report-activity.component';
import { AuthGuard } from './guards/auth-guard/auth.guard';

export const routes: Routes = [
  {path: '', component: LandingPageComponent},
  {path: 'register', component: RegistrationComponent},
  {path: 'home', component: HomeComponent,
    children: [
      {path: 'profile', component:ProfileComponent , canActivate: [AuthGuard]},
      {path: 'client-preferences', component: ClientPreferencesComponent , canActivate: [AuthGuard]},
      {path: 'client-portfolio', component: PortfolioComponent , canActivate: [AuthGuard]},
      {path: 'client-trading-history', component: TradingHistoryComponent , canActivate: [AuthGuard]},
      {path: 'report-activity', component: ReportActivityComponent , canActivate: [AuthGuard]},
      {path: 'client-preferences', component: ClientPreferencesComponent , canActivate: [AuthGuard]},
    ],
    canActivate: [AuthGuard]
  },
  {path:'**', redirectTo:'/'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
