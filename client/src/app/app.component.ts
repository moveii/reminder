import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {PushNotificationService} from './service/push-notification.service';
import {UserService} from './service/user.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'reminder';
  username = new Observable<string>();

  constructor(private router: Router, private pushNotificationService: PushNotificationService, private userService: UserService) {
  }

  ngOnInit(): void {
    this.username = this.userService.getUsernameAsObservable();

    this.userService.username().subscribe(username => {
      this.userService.setUsername(username.username);
    });

    this.pushNotificationService.requestPermission();
  }

  /**
   * Logs the user out by removing the token and navigating back to the login form.
   */
  logout() {
    this.userService.removeUsername();
    window.localStorage.removeItem('token');
    this.router.navigateByUrl('login');
  }

  /**
   * Returns true when the user is logged in.
   */
  loggedin(): boolean {
    return window.localStorage.getItem('token') !== null;
  }

  /**
   * Navigates to the [[ChangePasswordComponent]].
   */
  changePassword() {
    this.router.navigateByUrl('user/edit');
  }
}
