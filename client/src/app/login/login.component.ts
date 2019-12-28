import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../service/user.service';

/**
 * Contains all logic for the login component.
 */

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  hide = true;

  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService) {
  }

  /**
   * Sends the entered username and password to the server for login. The token will be set and the user will be navigated to the reminders.
   */
  onSave() {
    if (this.loginForm.invalid) {
      return;
    }

    const user = {
      username: this.loginForm.controls.username.value,
      password: this.loginForm.controls.password.value
    };

    this.userService.login(user).subscribe(data => {
      this.userService.setUsername(data.username);
      window.localStorage.setItem('token', data.token);
      this.router.navigate(['reminder']);
    }, error => {
      if (error.status === 401) {
        this.loginForm.controls.username.setErrors({unauthorized: true});
        this.loginForm.controls.password.setErrors({unauthorized: true});
      }
    });
  }

  /**
   * Removes the token and initializes the formGroup for input validation.
   */
  ngOnInit() {
    window.localStorage.removeItem('token');
    this.loginForm = this.formBuilder.group({
      username: [null, [Validators.required, Validators.minLength(6)]],
      password: [null, [Validators.required, Validators.minLength(12)]]
    });
  }

  /**
   * Returns the error message for the username input form.
   * @returns the error message for the username input form
   */
  getErrorMessageUsername(): string {
    const username = this.loginForm.controls.username;

    if (username.hasError('required')) {
      return 'Ein Nutzername muss angegeben werden';
    }

    if (username.hasError('minlength')) {
      return 'Der Nutzername muss mindestens sechs Zeichen lang sein.';
    }
  }

  /**
   * Returns the error message for the password input form.
   * @returns the error message for the password input form
   */
  getErrorMessagePassword(): string {
    const password = this.loginForm.controls.password;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';
    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }
  }

  /**
   * When username or password is changed, both errors will be removed.
   */
  inputChanged() {
    if (this.loginForm.controls.username.hasError('unauthorized') || this.loginForm.controls.password.hasError('unauthorized')) {
      this.loginForm.controls.username.setErrors(null);
      this.loginForm.controls.password.setErrors(null);
    }
  }
}
