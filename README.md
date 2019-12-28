# reminder
A reminder web application which uses predefined templates to support natural language input.

Note: The application currently only supports German input.  

## Usage
First, you need to create an account. The username must be at least six and the password must be at least twelve characters long. 

![Registration Screen](https://user-images.githubusercontent.com/51756146/71550199-f217cc80-29ca-11ea-9991-b8a7c8b925ee.PNG)

When you're logged in, you can create a new reminder by writing normal sentences. Furthermore, you can see your username 
in the right upper corner. When clicking on the icon next to it, you can access the user menu. Here you can change your 
password our simply log out. You will stay logged in for 24 hours. Afterwards, you need to enter your credentials again. 

![Reminder Input](https://user-images.githubusercontent.com/51756146/71550200-f348f980-29ca-11ea-9475-bc5e9d06b20a.PNG)

You can save the entered text by pressing enter or by pressing the button next to the input field.

![Reminder Saved](https://user-images.githubusercontent.com/51756146/71550201-f47a2680-29ca-11ea-91b1-0a578e2192d0.PNG)

As you can see, the key elements of the inputted text have been understood by the application. In this example, on December
30, 2019 at 2:30 p.m. a notification will be sent to remind you.

By clicking on the reminder, you can edit it. Pressing the button underneath will save the data.

![Reminder Edit](https://user-images.githubusercontent.com/51756146/71550202-f5ab5380-29ca-11ea-97b8-c2bf647488a0.PNG)

In addition, you can delete the reminder by pressing the delete icon. The search bar above the reminder list lets you search
for reminders (only by text) and the toggle next to it hides or shows already due reminders.

## Deployment
Make sure you have at least [Maven 3](https://maven.apache.org/download.cgi) and at least [Node.js v10.15.3](https://nodejs.org/en/) installed.
