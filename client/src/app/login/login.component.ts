import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../service/user.service';

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

  onSave() {
    if (this.loginForm.invalid) {
      return;
    }

    const user = {
      username: this.loginForm.controls.username.value,
      password: this.loginForm.controls.password.value
    };

    this.userService.login(user).subscribe(data => {
      window.localStorage.setItem('token', data.token);
      this.router.navigate(['reminder']);
    });
  }

  ngOnInit() {
    window.localStorage.removeItem('token');
    this.loginForm = this.formBuilder.group({
      username: [null, [Validators.required, Validators.minLength(6)]],
      password: [null, [Validators.required, Validators.minLength(12)]]
    });
  }

  getErrorMessageUsername(): string {
    const username = this.loginForm.controls.username;

    if (username.hasError('required')) {
      return 'Ein Nutzername muss angegeben werden';
    }

    if (username.hasError('minlength')) {
      return 'Der Nutzername muss mindestens sechs Zeichen lang sein.';
    }
  }

  getErrorMessagePassword(): string {
    const password = this.loginForm.controls.password;

    if (password.hasError('required')) {
      return 'Ein Passwort muss angegeben werden';

    }

    if (password.hasError('minlength')) {
      return 'Das Passwort muss mindestens 12 Zeichen lang sein.';
    }
  }
}
