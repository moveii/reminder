export class Reminder {

  constructor(text: string) {
    this.text = text;
  }

  private identifier: string;
  private text: string;
  private reminderDateTime: Date;

}
