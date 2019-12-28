import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {UserService} from './service/user.service';

/**
 * Intercepts HTTP-Requests to add the token to the header for authentication.
 */

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(public router: Router, private  userService: UserService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
    const token = window.localStorage.getItem('token');

    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token
        }
      });
    }

    return next.handle(request).pipe(
      catchError(err => {
          if (request.method !== 'PUT' && request.url !== '/authentication/user') {
            if (err.status === 401) {
              this.userService.removeUsername();
              window.localStorage.removeItem('token');
              this.router.navigateByUrl('login');
            }
          }
          throw err;
        }
      )
    );
  }
}
