import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../model/user';
import {Token} from '../model/token';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {
  }

  baseUrl = 'http://localhost:8080/users/';

  login(loginPayload): Observable<Token> {
    return this.http.post<Token>('http://localhost:8080/login', loginPayload);
  }

  registerUser(user: User): Observable<User> {
    return this.http.post<User>(this.baseUrl, user);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(this.baseUrl + user.username, user);
  }
}
