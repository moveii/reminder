import {Routes} from '@angular/router';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component';
import {ReminderComponent} from './reminder/reminder.component';
import {AuthGuard} from './authguard';
import {ReminderGuard} from './reminderguard';
import {ChangePasswordComponent} from './change-password/change-password.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent, canActivate: [ReminderGuard]},
  {path: 'registration', component: RegistrationComponent, canActivate: [ReminderGuard]},
  {path: 'reminder', component: ReminderComponent, canActivate: [AuthGuard]},
  {path: 'user/edit', component: ChangePasswordComponent, canActivate: [AuthGuard]},
  {path: '', component: LoginComponent, canActivate: [ReminderGuard]}
];
