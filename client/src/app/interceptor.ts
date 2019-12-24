import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';

/**
 * Intercepts HTTP-Requests to add the token to the header for authentication.
 */

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(public router: Router) {
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
          if (err.status === 401) {
            window.localStorage.removeItem('token');
            this.router.navigateByUrl('login');
          }
          throw err;
        }
      )
    );
  }
}
