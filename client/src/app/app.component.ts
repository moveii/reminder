import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'reminder';

  constructor(public router: Router) {
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
