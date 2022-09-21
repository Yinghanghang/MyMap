# MyMap

An android app that 
1) displays a Google map and allow users to switch between different views;
2) contains a floating action button (FAB) that will display the current location when the user clicks it.
3) has an SQLite database and a content provider so that:
• On clicking the Map, a marker will be drawn at the taped location, and the corresponding latitude or longitude coordinates with map zoom level will be saved in the database in background.
• On long-clicking, all makers will be cleared from the map, and all data will be removed from the database in background.
• On restarting the app, the saved locations are retrieved from the database in background and redrawn on the Map.
4) Restore setting when orientation changed/app restarted.

<img width="499" alt="Screen Shot 2022-09-21 at 3 47 54 PM" src="https://user-images.githubusercontent.com/71808318/191623710-6e303a71-fb8e-4fe8-9d09-ad096a8c1f74.png">
