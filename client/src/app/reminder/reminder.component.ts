import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from '../service/http.service';
import {Reminder} from '../dto/reminder';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-reminder',
  templateUrl: './reminder.component.html',
  styleUrls: ['./reminder.component.css']
})
export class ReminderComponent implements OnInit {

  dataSource: MatTableDataSource<Reminder> = new MatTableDataSource<Reminder>();
  displayedColumns: string[] = ['text', 'date'];

  private data: Reminder[] = [];

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(public httpService: HttpService) {
  }

  ngOnInit() {
    this.dataSource.sort = this.sort;
    this.httpService.findAllRemindersByDateAndTime().subscribe((value: Reminder[]) => {
      this.data = value;
      this.dataSource.data = this.data;
    });
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  public sendReminder(reminderText: string): void {
    const reminder = new Reminder(reminderText);
    this.httpService.createReminder(reminder).subscribe(value => {
      this.data.push(value);
      this.dataSource.data = this.data;
    });
  }
}
