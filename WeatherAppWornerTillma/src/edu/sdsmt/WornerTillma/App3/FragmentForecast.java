package edu.sdsmt.WornerTillma.App3;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentForecast extends Fragment
{
        private ForecastLocation location;
        private Forecast forecast;
        private View view;

        @Override
        public void onCreate(Bundle argumentsBundle)
        {
                super.onCreate(argumentsBundle);
                
                location = this.getArguments().getParcelable(Common.LOCATION_KEY);
                forecast = this.getArguments().getParcelable(Common.FORECAST_KEY);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
                View rootView = inflater.inflate(R.layout.fragment_forecast, null);
                this.view = rootView;
                
                int visibility;
                
                if(this.location.ZipCode == null || this.forecast.Temp == null)
                {
                	visibility = View.VISIBLE;
                }
                else
                {
                	visibility = View.INVISIBLE;
                }
                
                this.updateView(visibility);

                return rootView;
        }
		
		private void updateView(int visibility)
		{
			this.view.findViewById(R.id.layoutProgress).setVisibility(visibility);
			
			((TextView) this.view.findViewById(R.id.textViewLocation)).setText(location.City + ", " + location.State);
            ((TextView) this.view.findViewById(R.id.textViewTemp)).setText(forecast.Temp + "\u2109");
            ((TextView) this.view.findViewById(R.id.textViewFeelsLikeTemp)).setText(forecast.FeelsLike + "\u2109");
            ((TextView) this.view.findViewById(R.id.textViewHumidity)).setText(forecast.Humid + "%");
            ((TextView) this.view.findViewById(R.id.textViewChanceOfPrecip)).setText(forecast.PrecipChance + "%");
            ((TextView) this.view.findViewById(R.id.textViewAsOfTime)).setText(forecast.Time);
            ((ImageView) this.view.findViewById(R.id.imageForecast)).setImageBitmap(forecast.Image);
		}
}
