package edu.sdsmt.WornerTillma.App3;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentForecast extends Fragment implements IListeners
{
        public static final String LOCATION_KEY = "key_location";
        public static final String FORECAST_KEY = "key_forecast";
        
        private String zipCode;
        private ForecastLocation location;
        private Forecast forecast;
        private View view;

        @Override
        public void onCreate(Bundle argumentsBundle)
        {
                super.onCreate(argumentsBundle);
                
                this.zipCode = this.getArguments().getString(getString(R.string.ZipKey));
                location = new ForecastLocation();
                forecast = new Forecast();
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceStateBundle)
        {
                super.onSaveInstanceState(savedInstanceStateBundle);
                
                savedInstanceStateBundle.putParcelable(FragmentForecast.FORECAST_KEY, this.forecast);
                savedInstanceStateBundle.putParcelable(FragmentForecast.LOCATION_KEY, this.location);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
                View rootView = inflater.inflate(R.layout.fragment_forecast, null);
                this.view = rootView;
                
                if(savedInstanceState != null &&
                   savedInstanceState.containsKey(FragmentForecast.FORECAST_KEY) &&
                   savedInstanceState.containsKey(FragmentForecast.LOCATION_KEY))
                {
                	this.forecast = savedInstanceState.getParcelable(FragmentForecast.FORECAST_KEY);
                	this.location = savedInstanceState.getParcelable(FragmentForecast.LOCATION_KEY);
                	
                	this.updateView();
                }
                else
                {
	                // tidy this up
	                forecast.GetForecast(this.zipCode, this);
	                location.GetLocation(this.zipCode, this);
                }

                return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceStateBundle)
        {
                super.onActivityCreated(savedInstanceStateBundle);
        }

        @Override
        public void onDestroy()
        {
                
                super.onDestroy();
        }

		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) 
		{
            this.updateView();
		}

		@Override
		public void onForecastLoaded(Forecast forecast) 
		{
			this.updateView();
		}
		
		private void updateView()
		{
			((TextView) this.view.findViewById(R.id.textViewLocation)).setText(location.City + " " + location.State);
            ((TextView) this.view.findViewById(R.id.textViewTemp)).setText(forecast.Temp + "\u2109");
            ((TextView) this.view.findViewById(R.id.textViewFeelsLikeTemp)).setText(forecast.FeelsLike + "\u2109");
            ((TextView) this.view.findViewById(R.id.textViewHumidity)).setText(forecast.Humid + "%");
            ((TextView) this.view.findViewById(R.id.textViewChanceOfPrecip)).setText(forecast.PrecipChance + "%");
            ((TextView) this.view.findViewById(R.id.textViewAsOfTime)).setText(forecast.Time);
            ((ImageView) this.view.findViewById(R.id.imageForecast)).setImageBitmap(forecast.Image);

            this.view.findViewById(R.id.layoutProgress).setVisibility(View.INVISIBLE);
		}
}
