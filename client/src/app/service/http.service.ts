import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Reminder} from '../dto/reminder';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  private baseUrl = '/reminders';

  constructor(public httpClient: HttpClient) {
  }

  public findAllReminders(): Observable<Reminder> {
    return this.httpClient.get<Reminder>(this.baseUrl);
  }

  public findAllRemindersByDateAndTime(): Observable<Reminder[]> {
    return this.httpClient.get<Reminder[]>(this.baseUrl);
  }

  public createReminder(reminder: Reminder): Observable<Reminder> {
    return this.httpClient.post<Reminder>(this.baseUrl, reminder);
  }

  public modifyReminder(reminder: Reminder): Observable<Reminder> {
    return this.httpClient.put<Reminder>(this.baseUrl, reminder);
  }

  public deleteReminder(reminder: Reminder): void {
    this.httpClient.delete(this.baseUrl);
  }
}
