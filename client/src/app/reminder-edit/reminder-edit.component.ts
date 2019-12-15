import {Component, Input, OnInit} from '@angular/core';
import {Reminder} from '../dto/reminder';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from '@angular/material-moment-adapter';

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
export class ReminderEditComponent implements OnInit {

  minDate: Date = new Date();
  maxDate: Date = new Date(this.minDate.getFullYear() + 10, this.minDate.getMonth(), this.minDate.getDay());
  @Input() selectedReminder: Reminder;

  constructor() {
  }

  ngOnInit() {
  }
}
