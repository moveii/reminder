import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from '../service/http.service';
import {Reminder} from '../dto/reminder';
import {MatSort} from '@angular/material/sort';
import {FormControl} from '@angular/forms';

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

  ngOnInit() {
    this.httpService.findAllRemindersByDateAndTime().subscribe((value: Reminder[]) => {
      this.data = value;
      this.applyFilter();
    });

    this.startClockInterval();
  }

  private startClockInterval(): void {
    setInterval(() => {
      this.date = new Date();
    }, 1000);
  }

  applyFilter(): void {
    if (!this.filterInput || this.filterInput.length === 0) {
      this.filteredData = this.data;
    } else {
      this.filteredData = this.data.filter(reminder => reminder.text.toLowerCase().indexOf(this.filterInput.trim().toLowerCase()) !== -1);
    }
  }

  sendReminder(reminderText: string): void {
    const reminder = new Reminder(reminderText);
    this.httpService.createReminder(reminder).subscribe(value => {
      this.data.push(value);
    }, error => {
      if (error.status === 500) {
        this.inputForm.setErrors({template: true});
      } else {
        console.error(error);
      }
    });
  }

  deleteReminder(reminder: Reminder): void {
    this.httpService.deleteReminder(reminder).subscribe(() => {
      const index = this.data.indexOf(reminder);
      this.data.splice(index, 1);
      this.applyFilter();
    });
  }

  editReminder(reminder: Reminder): void {

  }
}
