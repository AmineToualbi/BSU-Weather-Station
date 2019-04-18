# BSUWeatherStation

This app was developed as an undergraduate research project during the Summer 2018 at Bridgewater State University, Massachusetts. 

The client, the geography department of the school, was interested in studying the correlation between student movements on campus and weather conditions. 

This app is an interface retrieving data collected by an Arduino-based weather station I built.


# Functionality

Different modules were developed for this project.

The main menu of the app presents those modules in a user-friendly interface implementing CardViews and popups. 

The main module is named "Data". This is where the data retrieval happens. 
The data is stored on a web server displaying information on a webpage in pure text. This data is retrieved using Google's Volley HTTP library. 
Information is presented in a swipable tab view with three tabs.

The "Location" module allows to save the location of the weather station when placed. It uses a third-party library named SimpleLocation to retrieve the GPS coordinates.   

The "Update" module displays a Toast notifying the user of when the next flow of data should be retrieved. 

The "Email" module allows to send a formatted report of all the data collected to the researcher. 

The "Website" module opens up the webpage showing the pure text of data collected by the weather station. 


# UI

![alt text](https://image.noelshack.com/fichiers/2018/33/7/1534705148-screenshot-2.png)

![alt text](https://image.noelshack.com/fichiers/2018/33/7/1534705148-screenshot-1.png)

![alt text](https://image.noelshack.com/fichiers/2018/33/7/1534705148-screenshot-3.png)


# Play Store

https://play.google.com/store/apps/details?id=com.myapps.toualbiamine.bsuweatherstation


