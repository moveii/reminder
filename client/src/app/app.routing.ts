import {Routes} from '@angular/router';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component';
import {ReminderComponent} from './reminder/reminder.component';
import {AuthGuard} from './authguard';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'reminder', component: ReminderComponent, canActivate: [AuthGuard]},
  {path: '', component: LoginComponent}
];
