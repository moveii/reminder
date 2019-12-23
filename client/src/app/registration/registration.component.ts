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

  form: FormGroup;

  ngOnInit() {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

  }

  onSubmit() {
    this.userService.register(this.form.value).subscribe((user: User) => {
      if (user.username === this.form.value.username) {
        this.userService.login(this.form.value).subscribe((token: Token) => {
          window.localStorage.setItem('token', token.token);
          this.router.navigate(['reminder']);
        });
      }
    });
  }
}
