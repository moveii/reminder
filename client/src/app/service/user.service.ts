import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../dto/user';
import {Token} from '../dto/token';
import {UpdateUser} from '../dto/update-user';

/**
 * Contains all HTTP-Request and -Response logic for authentication
 */

@Injectable()
export class UserService {

  private usernameSubject = new BehaviorSubject<string>('');

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

  /**
   * Sends a HTTP-PUT-Request to the server to change the user's password.
   * @returns the observable wrapping the user fetched from the server to subscribe
   */
  changePassword(user: UpdateUser): Observable<User> {
    return this.http.put<User>('/authentication/user', user);
  }

  /**
   * Sends a HTTP-GET-Request to the server to get the current username.
   * @returns the observable wrapping the user fetched from the server to subscribe
   */
  username(): Observable<User> {
    return this.http.get<User>('/authentication/user');
  }

  /**
   * Sets the username to the given username.
   * @param username the new username
   */
  setUsername(username: string) {
    this.usernameSubject.next(username);
  }

  /**
   * Sets the username to an empty string.
   */
  removeUsername() {
    this.usernameSubject.next('');
  }

  /**
   * Returns the username as an observable so components can subscribe to it.
   * @returns the username as an observable
   */
  getUsernameAsObservable(): Observable<string> {
    return this.usernameSubject.asObservable();
  }
}
