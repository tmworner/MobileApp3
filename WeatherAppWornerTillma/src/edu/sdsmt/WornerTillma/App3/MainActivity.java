package edu.sdsmt.WornerTillma.App3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
/**
 * The activity from which the entire app runs.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This is the main entry point for the app.  It stores a Forecast object and a ForecastLocation
 * 		object which it passes to the fragment to display.  It takes care of storing and retrieving
 * 		that data when the activity lifecycle makes it necessary.  It holds the async task callbacks
 * 		and sends the intents to show toast when an exception is caught.
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public class MainActivity extends Activity implements IListeners
{
	private String[] citiesArray; // class's array of cities
    private Forecast forecast; // class's forecast object
    private ForecastLocation location; // class's location object
    private String zipCode; // string to store the zip code
    private boolean endOfLifecycle; // detects "true" end of async task for lifecycle events

	/**
	 * Calls the super class's create function, gets the first entry from the city array
	 * and its Zip Code. If there is a saved instance, get it's information and redisplay.
	 * Otherwise, get new information and recreate the forecast. Then show the forecast.
	 * @param savedInstanceState The saved state of a previous runtime
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.endOfLifecycle = false;
        
        // Get City array from resources and get the zip code for the first city
        this.citiesArray = getResources().getStringArray(R.array.cityArray);
        this.zipCode = TextUtils.split(this.citiesArray[0], "\\|")[0];
        
        // if there is a saved instance that contains valid information
        if(savedInstanceState != null &&
 	       savedInstanceState.containsKey(Common.FORECAST_KEY) &&
 	       savedInstanceState.containsKey(Common.LOCATION_KEY))
 	    {
        	// get the savedState's information from the parcel
 	    	this.forecast = savedInstanceState.getParcelable(Common.FORECAST_KEY);
 	    	this.location = savedInstanceState.getParcelable(Common.LOCATION_KEY);
 	    }
        // otherwise
 	    else
 	    {
 	    	// create new Forecast and ForecastLocation objects
 	    	this.forecast = new Forecast();
 	    	this.location = new ForecastLocation();
 	    	
 	    	// try to get location and forecast information
 	        this.forecast.GetForecast(this.zipCode, this);
 	        this.location.GetLocation(this.zipCode, this);
 	    }

        // By default, first element is "favorite" city, go get location.
        // TextUtils.split() takes a regular expression and in the case
        // of a pipe delimiter, it needs to be escaped.
        showForecast();
    }

	/**
	 * Handles restoring instance state. If there is a saved state, gets that information.
	 * Otherwise, gets all new information for the forecast.
	 * @param savedInstanceState The saved state of a previous runtime
	 */
    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		// if there is a saved instance that contains valid information
		if(savedInstanceState != null &&
	       savedInstanceState.containsKey(Common.FORECAST_KEY) &&
	       savedInstanceState.containsKey(Common.LOCATION_KEY))
	    {
			// get the savedState's information from the parcel
	    	this.forecast = savedInstanceState.getParcelable(Common.FORECAST_KEY);
	    	this.location = savedInstanceState.getParcelable(Common.LOCATION_KEY);
	    }
		// otherwise
	    else
	    {
	    	// create new Forecast and ForecastLocation objects
	    	this.forecast = new Forecast();
 	    	this.location = new ForecastLocation();
 	    	
 	    	// try to get location and forecast information
 	        this.forecast.GetForecast(this.zipCode, this);
	        this.location.GetLocation(this.zipCode, this);
	    }
	}

	/**
	 * Calls the super savedInstanceState and puts the forecast and location
	 * in the bundle outState.
	 * @param outState The state of the current runtime to be saved.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		
		// if the temperature is valid and the zip code is valid
		if(this.forecast.Temp != null && this.location.ZipCode != null)
		{
			// put the information in the bundle so it can be restored when the lifecycle restarts
			outState.putParcelable(Common.FORECAST_KEY, this.forecast);
			outState.putParcelable(Common.LOCATION_KEY, this.location);
		}
	}

	/**
	 * Shows the forecast. Creates a new bundle object and uses it as the argument
	 * for the fragment. Shows the fragment, replaces the old.
	 */
	private void showForecast()
    {
		// if the end of lifecycle holder is false
		if(!this.endOfLifecycle)
		{
			// create a new bundle and put the forecast and location in it
	        Bundle bundle = new Bundle();
	        bundle.putParcelable(Common.FORECAST_KEY, this.forecast);
	        bundle.putParcelable(Common.LOCATION_KEY, this.location);
	        
	        // create a new fragment and set the arguments to the bundle
	        FragmentForecast fragment = new FragmentForecast();
	        fragment.setArguments(bundle);
	        
	        // get a fragment manager and show the fragment
	        getFragmentManager().beginTransaction()
								.replace(R.id.fragmentFrameLayout, fragment)
								.commit();
		}
    }

	/**
	 * Gets the location. This is called when the async task to get location data
	 * completes and it is given the new location data.  If the zipCode is null
	 * then the call failed and the data was not acquired, so it sends an intent to show toast.
	 * Regardless of the success of the async task, it displays the forecast (if the async task
	 * failed, the forecast data all shows as "null").
	 * @param forecastLocation The forecast location.
	 */
	@Override
	public void onLocationLoaded(ForecastLocation forecastLocation) 
	{
		this.location = forecastLocation; // set the location object
		
		// if the zipCode is null, send an intent to show Toast
		if(this.location.ZipCode == null)
		{
			this.SendExceptionIntent();
		}
		
		// show the forecast
		showForecast();
	}

	/**
	 * Gets the forecast data. This is called when the async task to get forecast
	 * completes and it is given the new forecast data.  If the Temp is null
	 * then the call failed and the data was not acquired, so it sends an intent to show toast.
	 * Regardless of the success of the async task, it displays the forecast (if the async task
	 * failed, the forecast data all shows as "null").
	 * @param forecast The forecast information.
	 */
	@Override
	public void onForecastLoaded(Forecast forecast) 
	{
		this.forecast = forecast; // set the forecast object
		
		// if the temperature is null, send an intent to show Toast
		if(this.forecast.Temp == null)
		{
			this.SendExceptionIntent();
		}
		
		// show the forecast
		showForecast();
	}

	/**
	 * This is the method override that handles the onPause lifecycle event.
	 * The change of "endOfLifecycle" is necessary because rapid screen rotations may combine
	 * with async tasks in such a way as to call showForecast() when the activity is not
	 * "alive," which crashes the app.
	 */
	@Override
	protected void onPause() 
	{
		// call the listener's cancel methods
		this.location.CancelGetLocation();
		this.forecast.CancelGetForecast();
		
		// set the end of lifecycle value
		this.endOfLifecycle = true;
		
		super.onPause();
	}

	/**
	 * Sends a broadcast to be received elsewhere in this app which will show toast.
	 * Meant primarily for showing a message when a network issue is encountered.
	 */
	private void SendExceptionIntent()
    {
		// create a new intent object and send the broadcast
    	Intent intent = new Intent(Common.ExceptionIntent);
		sendBroadcast(intent, android.Manifest.permission.VIBRATE);
    }

}
