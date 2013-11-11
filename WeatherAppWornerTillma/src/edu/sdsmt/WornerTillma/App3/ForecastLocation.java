package edu.sdsmt.WornerTillma.App3;

public class ForecastLocation
{

        private static final String TAG = "App3_ForecastLocation";
        
        // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
        // NOTE:  See example JSON in doc folder.
        private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                     "&api_key=u6vtegq4c7p72xk5cdwpcwtw";
        

        public ForecastLocation()
        {
                ZipCode = null;
                City = null;
                State = null;
                Country = null;
        }

        public String ZipCode;
        public String City;
        public String State;
        public String Country;
}
