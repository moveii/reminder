export class Reminder {

  constructor(text: string) {
    this.text = text;
  }

  identifier: string;
  text: string;
  reminderDateTime: Date;

}
