/**
 * Contains all information necessary for displaying data and serves as the data transfer object.
 */
export class Reminder {

    constructor(text: string) {
        this.text = text;
    }

    identifier: string;
    text: string;
    reminderDateTime: Date;
    selected = false;

}
