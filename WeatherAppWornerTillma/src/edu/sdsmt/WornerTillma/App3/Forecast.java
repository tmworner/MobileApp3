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

public class Forecast implements Parcelable
{
	public Bitmap Image;
    public String Temp;
    public String FeelsLike;
    public String Humid;
    public String PrecipChance;
    public String Time;
	
    //private static final String TAG = "App3_Forecast";
    
    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    private String URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
                          "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
                          "&api_key=q3wj56tqghv7ybd8dy6gg4e7";
    
    private LoadForecast loadForecast;
    
    // http://developer.weatherbug.com/docs/read/List_of_Icons
    private String ImageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
    
    public Forecast()
    {
        this.Image = null;
    }
    
    public void GetForecast(String zip, IListeners listener)
    {
    	this.loadForecast = new LoadForecast(listener);
    	this.loadForecast.execute(this.URL, this.ImageURL, zip);
    }
    
    public boolean CancelGetForecast()
    {
    	if(this.loadForecast != null && this.loadForecast.getStatus() != AsyncTask.Status.FINISHED)
    		return this.loadForecast.cancel(true);
		return false;
    }

    private Forecast(Parcel parcel)
    {
        this.Image = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.Temp = parcel.readString();
        this.FeelsLike = parcel.readString();
        this.Humid = parcel.readString();
        this.PrecipChance = parcel.readString();
        this.Time = parcel.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(Image, 0);
        dest.writeString(this.Temp);
        dest.writeString(this.FeelsLike);
        dest.writeString(this.Humid);
        dest.writeString(this.PrecipChance);
        dest.writeString(this.Time);
    }

    public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>()
    {
        @Override
        public Forecast createFromParcel(Parcel pc)
        {
            return new Forecast(pc);
        }
        
        @Override
        public Forecast[] newArray(int size)
        {
            return new Forecast[size];
        }
    };

    public class LoadForecast extends AsyncTask<String, Void, Forecast>
    {
		//private Context context;
        private IListeners listener;

        private int bitmapSampleSize = -1;
        private String conditions;

        public LoadForecast(IListeners listener)
        {
            this.listener = listener;
            //this.context = context;
        }

        // params[0] = URL
        // params[1] = ImageURL
        // params[2] = Zip
        protected Forecast doInBackground(String... params)
        {
        	Forecast forecast = new Forecast();
        	
            try
            {
            	StringBuilder stringBuilder = new StringBuilder();
            	HttpClient client = new DefaultHttpClient();
            	HttpResponse response;
        	
        		response = client.execute(new HttpGet(String.format(params[0], params[2])));
        		if(response.getStatusLine().getStatusCode() == 200)
        		{
        			HttpEntity entity = response.getEntity();
        			InputStream content = entity.getContent();
        			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        			
        			String line;
        			while((line = reader.readLine()) != null)
        			{
        				stringBuilder.append(line);
        			}
        			
        			this.ReadJSON(forecast, stringBuilder.toString());
        			this.readIconBitmap(forecast, this.conditions, this.bitmapSampleSize);
        		}

            }
            catch (IllegalStateException e)
            {
            	Receiver.SetMessage(e.toString());
        		//Log.e(TAG, e.toString() + params[0]);
            }
            catch (Exception e)
            {
            	Receiver.SetMessage(e.toString());
        		//Log.e(TAG, e.toString());
            }

            return forecast;
        }
        
        private void ReadJSON(Forecast forecast, String json)
        {
        	try
        	{
        		JSONObject jToken = new JSONObject(json);
        		
        		if(jToken.has("forecastHourlyList"))
        		{
        			JSONObject fc = jToken.getJSONArray("forecastHourlyList").getJSONObject(0);
        			
        			forecast.Temp = fc.getString("temperature");
        			forecast.FeelsLike = fc.getString("feelsLike");
        			forecast.Humid = fc.getString("humidity");
        			forecast.PrecipChance = fc.getString("chancePrecip");
        			forecast.Time = fc.getString("dateTime");
        			Date date = new Date(Long.valueOf(forecast.Time));
        			SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
        			format.setTimeZone(TimeZone.getTimeZone("gmt"));
        			forecast.Time = format.format(date);
        			this.conditions = fc.getString("icon");
        		}
        	}
        	catch(JSONException e)
        	{
        		Receiver.SetMessage(e.toString());
        		//Log.e(Forecast.TAG, e.toString());
        	}
        }

        protected void onPostExecute(Forecast forecast)
        {
            this.listener.onForecastLoaded(forecast);
        }

        private void readIconBitmap(Forecast forecast, String conditionString, int bitmapSampleSize)
        {
            try
            {
                URL weatherURL = new URL(String.format(ImageURL, conditionString));

                BitmapFactory.Options options = new BitmapFactory.Options();
                if (bitmapSampleSize != -1)
                {
                    options.inSampleSize = bitmapSampleSize;
                }

                forecast.Image = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
            }
            catch (MalformedURLException e)
            {
            	Receiver.SetMessage(e.toString());
        		//Log.e(TAG, e.toString());
            }
            catch (IOException e)
            {
            	Receiver.SetMessage(e.toString());
        		//Log.e(TAG, e.toString());
            }
            catch (Exception e)
            {
            	Receiver.SetMessage(e.toString());
        		//Log.e(TAG, e.toString());
            }
        }
    }
}
