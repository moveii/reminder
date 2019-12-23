import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../dto/user';
import {Token} from '../dto/token';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {
  }

  baseUrl = 'http://localhost:8080/users/';

  login(user: User): Observable<Token> {
    return this.http.post<Token>('/authentication/login', user);
  }

  register(user: User): Observable<User> {
    return this.http.post<User>('/authentication/register', user);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(this.baseUrl + user.username, user);
  }
}
