import {Component, Input} from '@angular/core';
import {Reminder} from '../dto/reminder';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from '@angular/material-moment-adapter';
import {HttpService} from '../service/http.service';
import {ReminderComponent} from '../reminder/reminder.component';

export const MY_FORMATS = {
  parse: {
    dateInput: 'LL',
  },
  display: {
    dateInput: 'LL',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

/**
 * Contains the logic for displaying all editing forms.
 */

@Component({
  selector: 'app-reminder-edit',
  templateUrl: './reminder-edit.component.html',
  styleUrls: ['./reminder-edit.component.css'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },

    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ],
})
export class ReminderEditComponent {

  minDate: Date = new Date();
  maxDate: Date = new Date(this.minDate.getFullYear() + 10, this.minDate.getMonth(), this.minDate.getDay());
  @Input() selectedReminder: Reminder;
  @Input() reminderComponent: ReminderComponent;

  constructor(public httpService: HttpService) {
  }

  /**
   * Calls the HTTP-Service to modify the reminder. This function automatically refreshes the filter and the displayed data.
   *
   * @param identifier the identifier of the reminder
   * @param date the date of the reminder
   * @param time the time of the reminder
   * @param text the text of the reminder
   */
  save(identifier, date, time, text) {
    const reminder = new Reminder(text);
    reminder.identifier = identifier;

    date = new Date(date);
    date.setUTCDate(date.getDate());
    date.setUTCHours(time.split(':')[0]);
    date.setUTCMinutes(time.split(':')[1]);
    reminder.reminderDateTime = date.toISOString();

    this.httpService.modifyReminder(reminder).subscribe(value => {
      this.selectedReminder.text = value.text;
      this.selectedReminder.reminderDateTime = new Date(value.reminderDateTime);
      this.reminderComponent.applyFilter();
    });
  }
}
