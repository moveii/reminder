import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

/**
 * Contains all logic for sending push notifications to the user.
 */

@Injectable()
export class PushNotificationService {

  public permission: Permission;

  constructor() {
    this.permission = PushNotificationService.isSupported() ? 'default' : 'denied';
  }

  /**
   * Returns true if notifications are supported in this environment. False otherwise.
   * @returns true if notifications are supported in this environment. False otherwise
   */
  private static isSupported(): boolean {
    return 'Notification' in window;
  }

  /**
   * Requests permission from the user for sending push notifications.
   */
  requestPermission(): void {
    if (PushNotificationService.isSupported()) {
      Notification.requestPermission(status =>
        this.permission = status
      );
    }
  }

  /**
   * Generates a push notification from the given data.
   * @param data the data containing the information
   */
  generateNotification(data: any): void {
    const options = {
      body: data.alertContent,
      icon: 'favicon.ico'
    };
    this.create(data.title, options).subscribe();
  }

  /**
   * Creates a new notification with the given title and options.
   * @param title the title of the notification
   * @param options the options of the notification
   */
  private create(title: string, options ?: PushNotification): any {
    return new Observable(obs => {
      if (!PushNotificationService.isSupported()) {
        console.log('Notifications are not available in this environment');
        obs.complete();
      }
      if (this.permission !== 'granted') {
        console.log('The user hasn\'t granted permission to send push notifications');
        obs.complete();
      }
      const notify = new Notification(title, options);
      notify.onshow = event => {
        return obs.next({
          notification: notify,
          event
        });
      };
      notify.onclick = event => {
        return obs.next({
          notification: notify,
          event
        });
      };
      notify.onerror = event => {
        return obs.error({
          notification: notify,
          event
        });
      };
      notify.onclose = () => {
        return obs.complete();
      };
    });
  }
}

export declare type Permission = 'denied' | 'granted' | 'default';

export interface PushNotification {
  body?: string;
  icon?: string;
  tag?: string;
  data?: any;
  renotify?: boolean;
  silent?: boolean;
  sound?: string;
  noscreen?: boolean;
  sticky?: boolean;
  dir?: 'auto' | 'ltr' | 'rtl';
  lang?: string;
  vibrate?: number[];
}
