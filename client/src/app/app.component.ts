import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {PushNotificationService} from './service/push-notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'reminder';

  constructor(private router: Router, private pushNotificationService: PushNotificationService) {
  }

  ngOnInit(): void {
    this.pushNotificationService.requestPermission();
  }

  /**
   * Logs the user out by removing the token and navigating back to the login form.
   */
  logout() {
    window.localStorage.removeItem('token');
    this.router.navigateByUrl('login');
  }

  /**
   * Returns true when the user is logged in.
   */
  loggedin(): boolean {
    return window.localStorage.getItem('token') !== null;
  }
}
