/**
 * App3 (Weather Viewer) shows a forecast acquired from WeatherBug.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		App Name: Weather Viewer<br>
 * 		Package Name: edu.sdsmt.WornerTillma.App3<br>
 * 		Class: Mobile Computing, CSC 492<br>
 * 		Professor: Dr. Logar, Brian Butterfield<br>
 * 		Due: November 18, 2013<br><br>
 * 
 * 		This app displays a weather forecast.  When it is opened, it uses async tasks
 * 		to get location and forecast data from WeatherBug based off of a stored zip
 * 		code.  Once the data is acquired, it displays the icon corresponding to the
 * 		forecast, the city and state associated with the given zip code, and some basic
 * 		forecast data (including temperature, what the temperature feels like, humidity,
 * 		chance of precipitation, and the time the forecast is for).  If the call to
 * 		WeatherBug fails, "null" is shown for all values and a message is displayed
 * 		via Toast.
 *  	<br><br>
 *  
 *  	A large portion of the code for this app comes from Brian Butterfield's examples, modified
 *  	to suit the requirements of this app.
 * 		</div>
 * </p>
 *
 * <p>
 * <div style="font-weight:bold">
 * Code Files:
 * </div>
 * 		<div style="padding-left:3em">
 * 		Common.java
 * 		Forecast.java
 * 		ForecastLocation.java
 * 		FragmentForecast.java
 * 		IListeners.java
 * 		MainActivity.java<br>
 * 		Receiver.java
 * 		</div>
 * </p>
 * 
 * <p>
 * <div style="font-weight:bold">
 * Custom xml Files:
 * </div>
 * 		<div style="padding-left:3em; font-style:italic">
 * 		Layouts:
 * 		<div style="padding-left:3em; font-style:normal">
 * 		activity_main.xml<br>
 * 		fragment_forecast.xml
 * 		</div>
 * 
 * 		Other:
 * 		<div style="padding-left:3em; font-style:normal">
 * 		strings.xml<br>
 * 		</div>
 * 
 * 		</div>
 * </p>
 * 
 * <p>
 * <div style="font-weight:bold">
 * Rough Development Timeline:
 * </div>
 * 		<div style="padding-left:3em">
 * 		November 11: Added Brian's provided code.<br>
 * 		November 13: Made the code (almost) fully functional.<br>
 * 		November 14: Fixed the display of the time the forecast is for.<br>
 * 		November 15: Fixed some bugs related to rotating the screen and async tasks.<br>
 * 		</div>
 * </p>
 * 
 * @author Teresa Worner and James Tillma
 */
package edu.sdsmt.WornerTillma.App3;