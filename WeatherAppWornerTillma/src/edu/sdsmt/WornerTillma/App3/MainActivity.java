package edu.sdsmt.WornerTillma.App3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends Activity implements IListeners
{
	private String[] citiesArray;
    private Forecast forecast;
    private ForecastLocation location;
    private String zipCode;
    private boolean endOfLifecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.endOfLifecycle = false;
        
        // Get City array from resources.
        this.citiesArray = getResources().getStringArray(R.array.cityArray);
        this.zipCode = TextUtils.split(this.citiesArray[0], "\\|")[0];
        
        if(savedInstanceState != null &&
 	       savedInstanceState.containsKey(Common.FORECAST_KEY) &&
 	       savedInstanceState.containsKey(Common.LOCATION_KEY))
 	    {
 	    	this.forecast = savedInstanceState.getParcelable(Common.FORECAST_KEY);
 	    	this.location = savedInstanceState.getParcelable(Common.LOCATION_KEY);
 	    }
 	    else
 	    {
 	    	this.forecast = new Forecast();
 	    	this.location = new ForecastLocation();
 	    	
 	        this.forecast.GetForecast(this.zipCode, this);
 	        this.location.GetLocation(this.zipCode, this);
 	    }

        // By default, first element is "favorite" city, go get location.
        // TextUtils.split() takes a regular expression and in the case
        // of a pipe delimiter, it needs to be escaped.
        showForecast();
    }

    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		if(savedInstanceState != null &&
	       savedInstanceState.containsKey(Common.FORECAST_KEY) &&
	       savedInstanceState.containsKey(Common.LOCATION_KEY))
	    {
	    	this.forecast = savedInstanceState.getParcelable(Common.FORECAST_KEY);
	    	this.location = savedInstanceState.getParcelable(Common.LOCATION_KEY);
	    }
	    else
	    {
	    	this.forecast = new Forecast();
 	    	this.location = new ForecastLocation();
 	    	
 	        this.forecast.GetForecast(this.zipCode, this);
	        this.location.GetLocation(this.zipCode, this);
	    }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		
		if(this.forecast.Temp != null && this.location.ZipCode != null)
		{
			outState.putParcelable(Common.FORECAST_KEY, this.forecast);
			outState.putParcelable(Common.LOCATION_KEY, this.location);
		}
	}

	private void showForecast()
    {
		if(!this.endOfLifecycle)
		{
	        Bundle bundle = new Bundle();
	        bundle.putParcelable(Common.FORECAST_KEY, this.forecast);
	        bundle.putParcelable(Common.LOCATION_KEY, this.location);
	        
	        FragmentForecast fragment = new FragmentForecast();
	        fragment.setArguments(bundle);
	        
	        getFragmentManager().beginTransaction()
								.replace(R.id.fragmentFrameLayout, fragment)
								.commit();
		}
    }

	@Override
	public void onLocationLoaded(ForecastLocation forecastLocation) 
	{
		this.location = forecastLocation;
		
		if(this.location.ZipCode == null)
		{
			this.SendExceptionIntent();
		}
		
		showForecast();
	}

	@Override
	public void onForecastLoaded(Forecast forecast) 
	{
		this.forecast = forecast;
		
		if(this.forecast.Temp == null)
		{
			this.SendExceptionIntent();
		}
		
		showForecast();
	}

	@Override
	protected void onPause() 
	{
		this.location.CancelGetLocation();
		this.forecast.CancelGetForecast();
		this.endOfLifecycle = true;
		super.onPause();
	}
	
	private void SendExceptionIntent()
    {
    	Intent intent = new Intent(Common.ExceptionIntent);
		sendBroadcast(intent, android.Manifest.permission.VIBRATE);
    }

}
