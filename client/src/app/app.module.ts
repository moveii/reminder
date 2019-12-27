import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {ReminderComponent} from './reminder/reminder.component';
import {ReminderEditComponent} from './reminder-edit/reminder-edit.component';
import {LoginComponent} from './login/login.component';
import {RegistrationComponent} from './registration/registration.component';
import {RouterModule} from '@angular/router';
import {routes} from './app.routing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {
  _MatMenuDirectivesModule,
  MatButtonModule,
  MatCardModule,
  MatDatepickerModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatSortModule,
  MatTableModule,
  MatToolbarModule
} from '@angular/material';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FlexModule} from '@angular/flex-layout';
import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {UserService} from './service/user.service';
import {HttpService} from './service/http.service';
import {TokenInterceptor} from './interceptor';
import {AuthGuard} from './authguard';
import {ReminderGuard} from './reminderguard';
import {PushNotificationService} from './service/push-notification.service';


@NgModule({
  declarations: [
    AppComponent,
    ReminderComponent,
    ReminderEditComponent,
    LoginComponent,
    RegistrationComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    BrowserAnimationsModule,
    HttpClientModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    MatListModule,
    MatTableModule,
    MatSortModule,
    MatIconModule,
    FormsModule,
    FlexModule,
    ReactiveFormsModule,
    MatNativeDateModule,
    MatMomentDateModule,
    MatDatepickerModule,
    _MatMenuDirectivesModule,
    MatMenuModule
  ],
  providers: [
    UserService,
    HttpService,
    PushNotificationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    AuthGuard,
    ReminderGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
