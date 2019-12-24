import {Routes} from '@angular/router';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component';
import {ReminderComponent} from './reminder/reminder.component';
import {AuthGuard} from './authguard';
import {ReminderGuard} from './reminderguard';

export const routes: Routes = [
  {path: 'login', component: LoginComponent, canActivate: [ReminderGuard]},
  {path: 'registration', component: RegistrationComponent, canActivate: [ReminderGuard]},
  {path: 'reminder', component: ReminderComponent, canActivate: [AuthGuard]},
  {path: '', component: LoginComponent, canActivate: [ReminderGuard]}
];
