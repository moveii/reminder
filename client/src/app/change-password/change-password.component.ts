import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../service/user.service';
import {UpdateUser} from '../dto/update-user';
import {MatSnackBar} from '@angular/material';
import {Observable} from 'rxjs';

/**
 * Contains all the logic necessary for changing one's password.
 */

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  username = new Observable<string>();

  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService, private snackBar: MatSnackBar) {
  }

  changePasswordForm: FormGroup;

  oldHide = true;
  newHide = true;
  newHideConfirmation = true;

  /**
   * Checks if the old and new password do not match. Additionally, this method checks if the new password and the confirmation password are
   * equal.
   * @param formGroup the formGroup to validate
   */
  private static checkPasswords(formGroup: FormGroup) {
    const oldPassword = formGroup.controls.oldPassword.value;
    const newPassword = formGroup.controls.newPassword.value;
    const newPasswordConfirmation = formGroup.controls.newPasswordConfirmation.value;

    if (oldPassword === newPassword && newPassword != null) {
      formGroup.controls.newPassword.setErrors({same: true});
    } else if (formGroup.controls.newPassword.hasError('same')) {
      formGroup.controls.newPassword.setErrors(null);
    }

    if (newPassword !== newPasswordConfirmation) {
      formGroup.controls.newPasswordConfirmation.setErrors({notSame: true});
    } else if (formGroup.controls.newPasswordConfirmation.hasError('notSame')) {
      formGroup.controls.newPasswordConfirmation.setErrors(null);
    }

    return null;
  }

  /**
   * Sets the username and initializes the formGroup for input validation.
   */
  ngOnInit() {
    this.username = this.userService.getUsernameAsObservable();

    this.changePasswordForm = this.formBuilder.group({
      oldPassword: [null, [Validators.required, Validators.minLength(12)]],
      newPassword: [null, [Validators.required, Validators.minLength(12)]],
      newPasswordConfirmation: [null, [Validators.required, Validators.minLength(12)]]
    });

    this.changePasswordForm.setValidators(ChangePasswordComponent.checkPasswords);
  }

  /**
   * Changes the password of the user. Requires the username, the old password and the new password.
   *
   * @param username the username of the user
   * @param oldPassword the old password of the user
   * @param newPassword the new password of the user
   */
  changePassword(username, oldPassword, newPassword) {
    const user = new UpdateUser();
    user.username = username;
    user.oldPassword = oldPassword;
    user.newPassword = newPassword;

    this.userService.changePassword(user).subscribe(() => {
      this.router.navigate(['reminder']);
    }, error => {
      if (error.status === 401) {
        this.changePasswordForm.controls.oldPassword.setErrors({unauthorized: true});
      } else {
        this.snackBar.open('Leider ist etwas schief gelaufen', 'Verstanden');
      }
    });
  }

  /**
   * Returns the error message for the old password input form.
   * @returns the error message for the old password input form
   */
  getErrorMessageOldPassword(): string {
    const password = this.changePasswordForm.controls.oldPassword;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';
    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }

    if (password.hasError('unauthorized')) {
      return 'Das eingegebene Passwort stimmt nicht mit den Anmeldedaten überein.';
    }
  }

  /**
   * Returns the error message for the new password input form.
   * @returns the error message for the new password input form
   */
  getErrorMessageNewPassword(): string {
    const password = this.changePasswordForm.controls.newPassword;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';
    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }

    if (password.hasError('same')) {
      return 'Das alte und das neue Passwort dürfen nicht übereinstimmen.';
    }
  }

  /**
   * Returns the error message for the new confirmation password input form.
   * @returns the error message for the new confirmation password input form
   */
  getErrorMessageNewConfirmationPassword(): string {
    const password = this.changePasswordForm.controls.newPasswordConfirmation;

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
