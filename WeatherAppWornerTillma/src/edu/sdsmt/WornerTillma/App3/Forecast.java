package edu.sdsmt.WornerTillma.App3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * Gets the forecast information using the weather bug API.
 * 
 * <p>
 * <div style="font-weight:bold">
 * Description:
 * </div>
 * 		<div style="padding-left:3em">
 * 		This class supplies methods for getting the needed information from WeatherBug.
 * 		It also allows canceling of the request to get the information. This class
 * 		contains another inner class that is described more there.
 * 		</div>
 * </p>
 * 
 * @since November 10, 2013
 * @author James Tillma and Teresa Worner
 */
public class Forecast implements Parcelable
{	
	/** The icon associated with the forecast */
	public Bitmap Image;
	/** The temperature given in the forecast */
    public String Temp;
	/** What the temperature given in the forecast feels like */
    public String FeelsLike;
	/** The humidity levels given in the forecast */
    public String Humid;
	/** The chance of precipitation given in the forecast */
    public String PrecipChance;
	/** The time the forecast is for */
    public String Time;
	
    // string that holds the URL to get the forecast information from WeatherBug
    private String URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
                          "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
                          "&api_key=u6vtegq4c7p72xk5cdwpcwtw";
                          
    // the LoadForecast object
    private LoadForecast loadForecast;
    
    // http://developer.weatherbug.com/docs/read/List_of_Icons
    private String ImageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
    
	/**
	 * Constructor for the class
	 */
    public Forecast()
    {
        this.Image = null; // set the image to be null
    }
    
	/**
	 * Gets the forecast information
	 * @param zip The zipcode used as the location
	 * @param listener The IListener object.
	 */
    public void GetForecast(String zip, IListeners listener)
    {
    	//create a new instance of LoadForecast object and gets the forecast information
    	this.loadForecast = new LoadForecast(listener);
    	this.loadForecast.execute(this.URL, this.ImageURL, zip);
    }
    
	/**
	 * Cancels the request to get the forecast (method used during lifecycle events).
	 */
    public boolean CancelGetForecast()
    {
    	// if LoadForecast object isn't null and the async task hasn't finished, cancel the task
    	if(this.loadForecast != null && this.loadForecast.getStatus() != AsyncTask.Status.FINISHED)
    		return this.loadForecast.cancel(true);
		return false;
    }

	/**
	 * Constructor for the class. This sets values based on those passed in parcel.
	 * @param parcel Contains the values to set class values to.
	 */
    private Forecast(Parcel parcel)
    {
    	// get the data from the parcel
        this.Image = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.Temp = parcel.readString();
        this.FeelsLike = parcel.readString();
        this.Humid = parcel.readString();
        this.PrecipChance = parcel.readString();
        this.Time = parcel.readString();
    }

	/**
	 * Describes the contents of the class, this isn't entirely useful for this app.
	 */
    @Override
    public int describeContents()
    {
        return 0; // returns 0, not very useful but required
    }

	/**
	 * Writes class values to a parcel
	 * @param dest The destination parcel for the information.
	 * @param flags
	 */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	// write the class values toe the parcel
        dest.writeParcelable(Image, 0);
        dest.writeString(this.Temp);
        dest.writeString(this.FeelsLike);
        dest.writeString(this.Humid);
        dest.writeString(this.PrecipChance);
        dest.writeString(this.Time);
    }

	/**
	 * Creates a parcel object.
	 */
    public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>()
    {
    	/**
    	 * Creates and returns a forecast object based on passed in value.
    	 * @param pc The parcel to use to create the forecast object
    	 */
        @Override
        public Forecast createFromParcel(Parcel pc)
        {
            return new Forecast(pc); // return a new forecast object
        }
        
    	/**
    	 * Creates and returns an array of forecast objects.
    	 * @param size The size of the array to create
    	 */
        @Override
        public Forecast[] newArray(int size)
        {
            return new Forecast[size]; // return a new array of forecast objects
        }
    };

    /**
     * Class that is an AsyncTask to load the forecast.
     * 
     * <p>
     * <div style="font-weight:bold">
     * Description:
     * </div>
     * 		<div style="padding-left:3em">
     * 		This class has methods that: load the forecast (in the background), reads the JSON, 
     * 		calls the listener's onForecastLoaded method, and reads the bitmap image returned by
     * 		WeatherBug.
     * 		</div>
     * </p>
     * 
     * @since November 10, 2013
     * @author James Tillma and Teresa Worner
     */
    public class LoadForecast extends AsyncTask<String, Void, Forecast>
    {
        private IListeners listener; // the listener that holds the onForecastLoaded method

        private int bitmapSampleSize = -1; // set bitmap size
        private String conditions; // create string conditions

    	/**
    	 * Sets the class's listener value.
    	 * @param The listener to set it to
    	 */
        public LoadForecast(IListeners listener)
        {
            this.listener = listener; // set the class's listener object
        }

    	/**
    	 * Gets the forecast from the Weather Bug API.
    	 * @param params The strings used as input parameters to the HttpGet function call.
    	 */
        // params[0] = URL
        // params[1] = ImageURL
        // params[2] = Zip
        protected Forecast doInBackground(String... params)
        {
        	Forecast forecast = new Forecast();
        	
            try
            {
            	// create a string object and HttpClient
            	StringBuilder stringBuilder = new StringBuilder();
            	HttpClient client = new DefaultHttpClient();
            	HttpResponse response;
            	
            	// get information from the URL
            	response = client.execute(new HttpGet(String.format(params[0], params[2])));
        		if(response.getStatusLine().getStatusCode() == 200)
        		{
        			HttpEntity entity = response.getEntity();
        			InputStream content = entity.getContent();
        			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        			
        			// Get the data
        			String line;
        			while((line = reader.readLine()) != null)
        			{
        				stringBuilder.append(line);
        			}
        			
        			// Read the data
        			this.ReadJSON(forecast, stringBuilder.toString());
        			this.readIconBitmap(forecast, this.conditions, this.bitmapSampleSize);
        		}

            }
            catch (IllegalStateException e)
            {
            	// if an IllegalSTateException is trapped, set the receiver message
            	Receiver.SetMessage(e.toString());
            }
            catch (Exception e)
            {
            	// if a general exception is trapped, set the receiver message
            	Receiver.SetMessage(e.toString());
            }

            return forecast; // return the forecast object
        }
        
    	/**
    	 * Reads the required information from the JSON object
    	 * forecast The object in which to set the values
    	 * json The JSON object to use to set the values
    	 */
        private void ReadJSON(Forecast forecast, String json)
        {
        	try
        	{
        		// get a new JSON object
        		JSONObject jToken = new JSONObject(json);
        		
        		// if it has an hourly forecast
        		if(jToken.has("forecastHourlyList"))
        		{
        			// get the JSON array
        			JSONObject fc = jToken.getJSONArray("forecastHourlyList").getJSONObject(0);
        			
        			// populate class values based on the array
        			forecast.Temp = fc.getString("temperature");
        			forecast.FeelsLike = fc.getString("feelsLike");
        			forecast.Humid = fc.getString("humidity");
        			forecast.PrecipChance = fc.getString("chancePrecip");
        			forecast.Time = fc.getString("dateTime");
        			
        			// set the conditions string to be the name of the icon
        			this.conditions = fc.getString("icon");
        			
        			// change the returned time from a string to a date object
        			Date date = new Date(Long.valueOf(forecast.Time));
        			
        			// create a format for the date/time
        			SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
        			
        			// set the format's TimeZone (WeatherBug adjusts for time so set back to GMT)
        			format.setTimeZone(TimeZone.getTimeZone("gmt"));
        			
        			// apply the format to the forcast.timeObjcet
        			forecast.Time = format.format(date);
        		}
        	}
        	catch(JSONException e)
        	{
        		// if the JSONException is caught, set the receiver's message
        		Receiver.SetMessage(e.toString());
        	}
        }

    	/**
    	 * Calls the listener's onForecastLoaded method.
    	 * @param forecast The object to pass into the listener
    	 */
        protected void onPostExecute(Forecast forecast)
        {
        	// call the listener's onForecastLoaded method
            this.listener.onForecastLoaded(forecast);
        }

    	/**
    	 * Reads the icons bitmap and sets the bitmap image of the class.
    	 * @param forecast The object to set the values in
    	 * @param conditionString The string used as conditions in the URL creation
    	 * @param bitmapSamplkeSize The size of the bitmap image
    	 */
        private void readIconBitmap(Forecast forecast, String conditionString, int bitmapSampleSize)
        {
            try
            {
            	// get the bitmap image  from a URL
                URL weatherURL = new URL(String.format(ImageURL, conditionString));
                
                // create a bitmap options object
                BitmapFactory.Options options = new BitmapFactory.Options();
                if (bitmapSampleSize != -1)
                {
                    options.inSampleSize = bitmapSampleSize;
                }
                
                // set the class's bitmap value
                forecast.Image = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
            }
            // handle any possible exceptions by showing Toast
            catch (MalformedURLException e)
            {
            	Receiver.SetMessage(e.toString());
            }
            catch (IOException e)
            {
            	Receiver.SetMessage(e.toString());
            }
            catch (Exception e)
            {
            	Receiver.SetMessage(e.toString());
            }
        }
    }
}
