import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Reminder} from '../dto/reminder';

/**
 * Contains all HTTP-Request and -Response logic for Reminders
 */

@Injectable()
export class HttpService {

  private baseUrl = '/reminders/';

  constructor(public httpClient: HttpClient) {
  }

  /**
   * Sends a HTTP-GET-Request to the server to get all reminders.
   * @returns the observable wrapping all reminders fetched from the server to subscribe
   */
  public findAllRemindersByDateAndTime(): Observable<Reminder[]> {
    return this.httpClient.get<Reminder[]>(this.baseUrl);
  }

  /**
   * Sends a HTTP-POST-Request to the server to create the given reminder.
   * @param reminder the reminder to be created
   * @returns the observable wrapping the created reminder to subscribe
   */
  public createReminder(reminder: Reminder): Observable<Reminder> {
    return this.httpClient.post<Reminder>(this.baseUrl, reminder);
  }

  /**
   * Sends a HTTP-PUT-Request to the server to modify the given reminder.
   * @param reminder the reminder to be modified
   * @returns the observable wrapping the modified reminder to subscribe
   */
  public modifyReminder(reminder: Reminder): Observable<Reminder> {
    return this.httpClient.put<Reminder>(this.baseUrl, reminder);
  }

  /**
   * Sends a HTTP-DELETE-Request to the server to delete the given reminder by identifier.
   * @param reminder the reminder to be deleted
   * @returns the observable to subscribe
   */
  public deleteReminder(reminder: Reminder): Observable<any> {
    return this.httpClient.delete(this.baseUrl + `${reminder.identifier}`);
  }
}
