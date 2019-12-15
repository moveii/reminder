import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from '../service/http.service';
import {Reminder} from '../dto/reminder';
import {MatSort} from '@angular/material/sort';
import {FormControl} from '@angular/forms';

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

  @ViewChild(MatSort, {static: true}) sort: MatSort;
  private data: Reminder[] = [];

  constructor(public httpService: HttpService) {
  }

  /**
   * Fetches data and starts [[clockInterval]] on startup.
   */
  ngOnInit() {
    this.httpService.findAllRemindersByDateAndTime().subscribe((value: Reminder[]) => {
      this.data = value;
      this.applyFilter();
    });

    this.clockInterval();
  }

  /**
   * Refreshes the date every minute to handle alarm icons.
   */
  private clockInterval(): void {
    setInterval(() => {
      this.date = new Date();
    }, 1000);
  }

  /**
   * Applies the filter to the list to show only matching items.
   */
  applyFilter(): void {
    if (!this.filterInput || this.filterInput.length === 0) {
      this.filteredData = this.data;
    } else {
      this.filteredData = this.data.filter(reminder => reminder.text.toLowerCase().indexOf(this.filterInput.trim().toLowerCase()) !== -1);
    }
  }

  /**
   * Calls the HTTP-Service to create the reminder by the given text. This function automatically refreshes the filter
   * @param reminderText the text which contains the information from which the server creates a reminder
   */
  sendReminder(reminderText: string): void {
    const reminder = new Reminder(reminderText);
    this.httpService.createReminder(reminder).subscribe(value => {
      this.data.push(value);
      this.applyFilter();
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
   * Calls the HTTP-Service to modify the reminder. This function automatically refreshes the filter
   * and the [[ReminderEditComponent]]
   * @param reminder the reminder to be modified
   */
  editReminder(reminder: Reminder): void {

  }
}
