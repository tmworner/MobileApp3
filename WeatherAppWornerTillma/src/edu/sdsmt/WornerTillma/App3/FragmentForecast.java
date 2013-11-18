package edu.sdsmt.WornerTillma.App3;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * The fragment that displays the forecast.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This class controls the display of the forecast. It creates the fragment, inflates the
 * 		view, and then it updates the view with the proper information.
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public class FragmentForecast extends Fragment
{
    private ForecastLocation location; // class's forecastLocation object
    private Forecast forecast; // class's forecast object
    private View view; // class's view object

	/**
	 * The onCreate override for the fragment.
	 * @param argumentsBundle The bundle to use to create the fragment.
	 */
    @Override
    public void onCreate(Bundle argumentsBundle)
    {
        super.onCreate(argumentsBundle);
        
        // get the location and forecast objects that have been passed in to the fragments as arguments
        location = this.getArguments().getParcelable(Common.LOCATION_KEY);
        forecast = this.getArguments().getParcelable(Common.FORECAST_KEY);
    }

	/**
	 * Inflates the view and displays the information received from WeatherBug (if any).
	 * @param inflater Inflater used to create and show the fragment
	 * @param savedInstanceState Contains any saved state information from the last runtime.
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	// inflate the fragment and set the class's value
        View rootView = inflater.inflate(R.layout.fragment_forecast, null);
        this.view = rootView;
        
        // create an integer to represent visibility of the spinner on the screen
        int visibility;
        
        // if the zip code is invalid or the temperature is invalid, set the spinner to be visible
        if(this.location.ZipCode == null || this.forecast.Temp == null)
        {
        	visibility = View.VISIBLE;
        }
        // otherwise, set it to be invisible
        else
        {
        	visibility = View.INVISIBLE;
        }
        
        // update the fragment's display
        this.updateView(visibility);
        
        // return the fragment's rootView
        return rootView;
    }
	
	/**
	 * Updates the information in the fragment.
	 * @param visibiility The value to set the visibility of the loading circle to.
	 */
	private void updateView(int visibility)
	{
		// set the spinner's visibility
		this.view.findViewById(R.id.layoutProgress).setVisibility(visibility);
		
		// set the text for all of the text views in the fragment
		((TextView) this.view.findViewById(R.id.textViewLocation)).setText(location.City + ", " + location.State);
        ((TextView) this.view.findViewById(R.id.textViewTemp)).setText(forecast.Temp + "\u2109");
        ((TextView) this.view.findViewById(R.id.textViewFeelsLikeTemp)).setText(forecast.FeelsLike + "\u2109");
        ((TextView) this.view.findViewById(R.id.textViewHumidity)).setText(forecast.Humid + "%");
        ((TextView) this.view.findViewById(R.id.textViewChanceOfPrecip)).setText(forecast.PrecipChance + "%");
        ((TextView) this.view.findViewById(R.id.textViewAsOfTime)).setText(forecast.Time);
        ((ImageView) this.view.findViewById(R.id.imageForecast)).setImageBitmap(forecast.Image);
	}
}
