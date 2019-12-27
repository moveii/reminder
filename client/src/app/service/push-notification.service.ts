import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable()
export class PushNotificationService {

  constructor() {
    this.permission = PushNotificationService.isSupported() ? 'default' : 'denied';
  }

  public permission: Permission;

  private static isSupported(): boolean {
    return 'Notification' in window;
  }

  requestPermission(): void {
    if (PushNotificationService.isSupported()) {
      Notification.requestPermission(status =>
        this.permission = status
      );
    }
  }

  generateNotification(source: Array<any>): void {
    source.forEach((item) => {
      const options = {
        body: item.alertContent,
        icon: '../../favicon.ico'
      };
      this.create(item.title, options).subscribe();
    });
  }

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
