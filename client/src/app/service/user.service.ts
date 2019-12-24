import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../dto/user';
import {Token} from '../dto/token';

/**
 * Contains all HTTP-Request and -Response logic for authentication
 */

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {
  }

  /**
   * Sends a HTTP-POST-Request to the server to authenticate the user with the given credentials.
   * @returns the observable wrapping the token (if authenticated) fetched from the server to subscribe
   */
  login(user: User): Observable<Token> {
    return this.http.post<Token>('/authentication/login', user);
  }

  /**
   * Sends a HTTP-POST-Request to the server to register the user.
   * @returns the observable wrapping the registered user fetched from the server to subscribe
   */
  register(user: User): Observable<User> {
    return this.http.post<User>('/authentication/register', user);
  }
}
