import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from '../service/http.service';
import {Reminder} from '../dto/reminder';
import {MatSort} from '@angular/material/sort';
import {FormControl} from '@angular/forms';
import {PushNotificationService} from '../service/push-notification.service';
import {formatDate} from '@angular/common';

/**
 * Contains the logic for filtering, editing, deleting and creating reminders.
 */

@Component({
  selector: 'app-reminder',
  templateUrl: './reminder.component.html',
  styleUrls: ['./reminder.component.css']
})
export class ReminderComponent implements OnInit {

  date: Date = new Date();
  selectedReminder: Reminder;
  filterInput: string;
  filteredData: Reminder[] = [];
  inputForm: FormControl = new FormControl('');
  showAll = false;

  @ViewChild(MatSort, {static: true}) sort: MatSort;
  private data: Reminder[] = [];

  constructor(private httpService: HttpService, private pushNotificationService: PushNotificationService) {
  }

  /**
   * Fetches data and starts [[clockInterval]] on startup.
   */
  ngOnInit() {
    this.fetchReminders();
    this.clockInterval();
  }

  /**
   * Refreshes the date every minute to handle alarm icons.
   */
  private clockInterval(): void {
    setInterval(() => {
      this.date = new Date();

      this.data.forEach(value => {
        if (value.reminderDateTime.getTime() <= this.date.getTime() && value.reminderDateTime.getTime() + 1_000 > this.date.getTime()) {
          this.pushNotificationService.generateNotification([{
            title: value.text,
            alertContent: formatDate(value.reminderDateTime, 'dd. MMMM yyyy HH:mm', 'en-US')
          }]);
        }
      });
    }, 1_000);
  }

  /**
   * Applies the filter to the list to show only matching items.
   */
  applyFilter(): void {
    if (!this.filterInput || this.filterInput.length === 0) {
      this.filteredData = this.data.sort((a, b) => a.reminderDateTime.getTime() - b.reminderDateTime.getTime());
    } else {
      this.filteredData = this.data.filter(reminder => reminder.text.toLowerCase().indexOf(this.filterInput.trim().toLowerCase()) !== -1)
        .sort((a, b) => a.reminderDateTime.getTime() - b.reminderDateTime.getTime());
    }
  }

  /**
   * Calls the HTTP-Service to create the reminder by the given text. This function automatically refreshes the filter
   * @param reminderText the text which contains the information from which the server creates a reminder
   */
  sendReminder(reminderText: string): void {
    const reminder = new Reminder(reminderText);
    this.httpService.createReminder(reminder).subscribe(value => {
      const fetchedReminder = new Reminder(value.text);
      fetchedReminder.identifier = value.identifier;
      fetchedReminder.reminderDateTime = new Date(value.reminderDateTime);

      this.data.push(fetchedReminder);
      this.applyFilter();
      this.inputForm.setValue('');
    }, error => {
      if (error.status === 500) {
        this.inputForm.setErrors({template: true});
      } else {
        console.error(error);
      }
    });
  }

  /**
   * Calls the HTTP-Service to delete the reminder by the identifier. This function automatically refreshes the filter
   * and the [[ReminderEditComponent]]
   * @param reminder the reminder to be deleted
   */
  deleteReminder(reminder: Reminder): void {
    this.httpService.deleteReminder(reminder).subscribe(() => {
      const index = this.data.indexOf(reminder);
      this.data.splice(index, 1);

      this.selectedReminder = undefined;

      this.applyFilter();
    });
  }

  /**
   * Fetches the data according to [[showAll]]. If it's false, all reminders after the current date and time will be returned. Otherwise,
   * all reminders will be returned.
   */
  fetchReminders() {
    this.data = [];
    this.selectedReminder = undefined;
    if (this.showAll) {
      this.httpService.findAllReminders().subscribe((value: Reminder[]) => {
        for (const reminder of value) {
          const fetchedReminder = new Reminder(reminder.text);
          fetchedReminder.identifier = reminder.identifier;
          fetchedReminder.reminderDateTime = new Date(reminder.reminderDateTime);

          this.data.push(fetchedReminder);
        }
        this.applyFilter();
      });
    } else {
      this.httpService.findAllRemindersByDateAndTime().subscribe((value: Reminder[]) => {
        for (const reminder of value) {
          const fetchedReminder = new Reminder(reminder.text);
          fetchedReminder.identifier = reminder.identifier;
          fetchedReminder.reminderDateTime = new Date(reminder.reminderDateTime);

          this.data.push(fetchedReminder);
        }
        this.applyFilter();
      });
    }
  }
}
