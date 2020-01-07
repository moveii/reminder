# Reminder
A reminder web application which uses predefined templates to support natural language input.

Note: The application currently only fully supports German input.  

## Usage
First, you need to create an account. The username must be at least six and the password must be at least twelve characters long. 

![Registration Screen](https://user-images.githubusercontent.com/51756146/71550199-f217cc80-29ca-11ea-9991-b8a7c8b925ee.PNG)

When you're logged in, you can create a new reminder by writing normal sentences. Furthermore, you can see your username 
in the right upper corner. When clicking on the icon next to it, you can access the user menu. Here you can change your 
password our simply log out. You will stay logged in for 24 hours. Afterwards, you need to enter your credentials again. 

![Reminder Input](https://user-images.githubusercontent.com/51756146/71550201-f47a2680-29ca-11ea-91b1-0a578e2192d0.PNG)

You can save the entered text by pressing enter or by pressing the button next to the input field.

![Reminder Saved](https://user-images.githubusercontent.com/51756146/71550202-f5ab5380-29ca-11ea-97b8-c2bf647488a0.PNG)

As you can see, the key elements of the inputted text have been understood by the application. Additionally, 'NVS' has been replaced with 'Netzwerktechnik' (as defined in [replacements.rmd](server/src/main/resources/replacements.rmd)). In this example, on December
30, 2019 at 2:30 p.m. a notification will be sent to remind you.

Here are some example inputs:

* eine Woche vor dem 31.12.2019 um 23 Uhr Mathe zu machen
* eine Woche nach dem 31.12.2019 um 11 Uhr Mathe zu machen
* morgen in einer Woche NVS zu machen
* heute in einer Woche NVS zu machen
* heute in einer Woche um 12:01 Uhr NVS zu machen
* eine Woche vor dem 31.12.2019 Mathe zu machen
* eine Woche nach dem 31.12.2019 Mathe zu machen
* in einem Tag um 18 Uhr NVS abzugeben
* in einem Tag NVS abzugeben
* in einem Jahr NVS zu machen
* am 22.12.2019 NVS abzugeben
* am 22.12.2019 um 01:02 Uhr NVS abzugeben
* heute NVS zu machen
* morgen NVS zu machen
* übermorgen um 18:23 NVS zu machen
* um 18:23 NVS zu machen
* in einer minute NVS zu erledigen

By clicking on the reminder, you can edit it. Pressing the button underneath will save the data.

![Reminder Edit](https://user-images.githubusercontent.com/51756146/71550200-f348f980-29ca-11ea-9475-bc5e9d06b20a.PNG)

In addition, you can delete the reminder by pressing the delete icon. The search bar above the reminder list lets you search
for reminders (only by text) and the toggle next to it hides or shows already due reminders.

## Extensions

Don't forget to always rebuild and redeploy the application after making changes to the files.

### Replacements
In [`replacements.rmd`](server/src/main/resources/replacements.rmd), you can specify replacement words for other words. For instance, `Networking=NW` would mean that `NW` will be replaced with `Networking`. Replacement matching is case insensitive. So you can enter `nw` too. You can also specify word groups for replacement: `eating=go eating`, for instance.

### Definitions
In [`definitons.rmd`](server/src/main/resources/definitions.rmd), you can specify definitions or synonyms for system understood words and numbers. For instance, `1=one` would mean that the application understands `one` too. Definitions are case insensitive. You do not have to specify definitions or synonyms. You can use any of the following by default (list of all system understood words and digits):
* any number (1-2,147,483,647)
* minute
* hour
* day
* week
* month
* year
* today
* tomorrow
* dat (day after tomorrow)

### Templates
In [`templates.rmd`](server/src/main/resources/templates.rmd), you can specify custom templates so the application understands your input. For instance, with `[DURATION] [UNIT] before the [DATE] at [TIME] [TEXT];-` you would be able to enter something like `[Remind me] two days before the 31.12.2019 at 14:30 to go to the grocery store` ('two' and 'days' have to be specified in [`definitons.rmd`](server/src/main/resources/definitions.rmd)). The `-` after the semicolon tells the application that `[DATE]` must be subtracted with `[UNIT]` and `[DURATION]`. A `+` (and every other symbol) indicates an addition. 

* [DATE] can be a date (yyyy-MM-dd, dd-MM-yyyy, dd.MM.yyyy) or today, tomorrow or dat (day after tomorrow)
* [TIME] can be a time (HH:mm oder HH)
* [DURATION] can be any number
* [UNIT] can be minute, hour, day, week, month or year
* [TEXT] is the text (here the replacements will be used)

Only modify [`templates.rmd`](server/src/main/resources/templates.rmd) at your own risk.

## Prerequisite
Note: You only need to install the Java Runtime Environment ([JRE](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)) to execute the project. However, for building the jar, you require the following things:

Make sure you have at least [Java 13](https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html) ([Java 13 on Ubuntu](http://ubuntuhandbook.org/index.php/2019/10/how-to-install-oracle-java-13-in-ubuntu-18-04-16-04-19-04/)), at least [Maven 3](https://maven.apache.org/download.cgi) ([Maven 3 on Ubuntu](https://linuxize.com/post/how-to-install-apache-maven-on-ubuntu-18-04/)) and at least [Node.js v10.15.3](https://nodejs.org/en/) (installed by default on Ubuntu) installed.

Next, you need to install `Angular CLI`.
```
npm install -g @angular/cli
```

#### Local installation (only accessible via localhost)
After successfully installing Angular, you need to generate a keystore. For this, we use `Keytool`. It is included with Java. So if you have troubles executing the command, use the absolute path to the keytool.exe (in the java bin folder). 
Use the following command. **Do not change any of these parameters.** 
```
keytool -genkeypair -alias reminder -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore reminder.p12 -validity 3650 -storepass a9f14591-1c46-45fb-a489-946cf53d7f22
```
Copy the `reminder.p12` file in to `server/src/main/resources/keystore`. You have to create `keystore` manually.

Now you're ready to build the project.

#### Public installation (accessible via an ip address or domain)
In this case, you have to make minor changes to the application.
First, you need to remove the content of `server/src/main/resources/application.properties`. This will ensure, that the application will not try to use any local certificate. 

Second, you need to add your domain (not ip address!) to the security configuration. For this, navigate to `server/src/main/java/at/spengergasse/nvs/server/config/WebSecurityConfig.java` and find `         configuration.setAllowedOrigins(Collections.singletonList("https://localhost:4200"));`. You have to change `https://localhost:4200` to your domain. Otherwise, the requests will be blocked.

Now you're ready to build the project.

## Deployment
### Build the project
To build the project, you need to execute `mvn clean install` in the project **root folder**. Wait until finished.
Now the project is build. Navigate into `server/target` and find the `server.jar` (with a version number).

### Execute the project
If you execute the `server.jar`, the application will start. You can simply execute it with `java -jar server.jar`. However, if you close the session, the application will stop. If you don't want this behaviour, use `nohup java -jar server.jar &` instead. With `nohup java -jar server.jar > log.txt &` you can change the log output to a custom file (default is `nohup.out`).

The local time of the server will be used for the creation of reminders. Make sure that you set the timezone correctly.
