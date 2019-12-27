import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../service/user.service';
import {Token} from '../dto/token';
import {User} from '../dto/user';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService) {
  }

  registerForm: FormGroup;
  hide = true;
  hideConfirmation = true;

  private static checkPasswords(formGroup: FormGroup) {
    const password = formGroup.controls.password.value;
    const passwordConfirmation = formGroup.controls.passwordConfirmation.value;

    if (password !== passwordConfirmation) {
      formGroup.controls.passwordConfirmation.setErrors({notSame: true});
    } else {
      formGroup.controls.passwordConfirmation.setErrors(null);
    }

    return null;
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      username: [null, [Validators.required, Validators.minLength(6)]],
      password: [null, [Validators.required, Validators.minLength(12)]],
      passwordConfirmation: [null, [Validators.required, Validators.minLength(12)]]
    });

    this.registerForm.setValidators(RegistrationComponent.checkPasswords);
  }

  /**
   * Sends the entered username and password to the server for registration. The token will be set and the user will be navigated to the
   * reminders.
   */
  onSave() {
    this.userService.register(this.registerForm.value).subscribe((user: User) => {
      if (user.username === this.registerForm.value.username) {
        this.userService.login(this.registerForm.value).subscribe((token: Token) => {
          window.localStorage.setItem('token', token.token);
          this.router.navigate(['reminder']);
        });
      }
    }, error => {
      if (error.status === 409) {
        this.registerForm.controls.username.setErrors({duplicate: true});
      }
    });
  }

  /**
   * Returns the error message for the username input form.
   * @returns the error message for the username input form
   */
  getErrorMessageUsername(): string {
    const username = this.registerForm.controls.username;

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
    const password = this.registerForm.controls.password;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';
    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }
  }

  /**
   * Returns the error message for the confirmation password input form.
   * @returns the error message for the confirmation password input form
   */
  getErrorMessageConfirmationPassword(): string {
    const password = this.registerForm.controls.passwordConfirmation;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';
    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }

    if (password.hasError('notSame')) {
      return 'Die Passwörter stimmen nicht überein.';
    }
  }
}
