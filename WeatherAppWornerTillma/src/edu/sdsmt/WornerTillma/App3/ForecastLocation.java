package edu.sdsmt.WornerTillma.App3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

public class ForecastLocation implements Parcelable
{
	public String ZipCode;
    public String City;
    public String State;
    public String Country;
    
    //private static final String TAG = "App3_ForecastLocation";
    
    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    private String URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                 "&api_key=u6vtegq4c7p72xk5cdwpcwtw";
                                 
     private LoadForecastLocation loadLocation;
    

    public ForecastLocation()
    {
		this.ZipCode = null;
        this.City = null;
        this.State = null;
        this.Country = null;
    }
    
    public void GetLocation(String zip, IListeners listener)
    {
    	this.loadLocation = new LoadForecastLocation(listener);
    	this.loadLocation.execute(this.URL, zip);
    }
    
    public boolean CancelGetLocation()
    {
    	if(this.loadLocation != null && this.loadLocation.getStatus() != AsyncTask.Status.FINISHED)
    		return this.loadLocation.cancel(true);
		return false;
    }
    
    private ForecastLocation(Parcel parcel)
    {
        this.ZipCode = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.City = parcel.readString();
        this.State = parcel.readString();
        this.Country = parcel.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.ZipCode);
        dest.writeString(this.City);
        dest.writeString(this.State);
        dest.writeString(this.Country);
    }

    public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>()
    {
        @Override
        public ForecastLocation createFromParcel(Parcel pc)
        {
            return new ForecastLocation(pc);
        }
        
        @Override
        public ForecastLocation[] newArray(int size)
        {
            return new ForecastLocation[size];
        }
    };
    
    public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
        private IListeners listener;
        //private Context context;

        public LoadForecastLocation(IListeners listener)
        {
            this.listener = listener;
            //this.context = context;
        }

        // params[0] = URL
        // params[2] = Zip
        protected ForecastLocation doInBackground(String... params)
        {
        	ForecastLocation location = new ForecastLocation();
        
            try
            {
            	StringBuilder stringBuilder = new StringBuilder();
            	HttpClient client = new DefaultHttpClient();
            	HttpResponse response;
        	
        		response = client.execute(new HttpGet(String.format(params[0], params[1])));
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
        			
        			this.ReadJSON(location, stringBuilder.toString());
        		}

            }
        	catch(ClientProtocolException e)
        	{
        		Receiver.SetMessage(e.toString());
        		// Log.e(ForecastLocation.TAG, e.getMessage());
        	}
        	catch(IOException e)
        	{
        		Receiver.SetMessage(e.toString());
        		// Log.e(ForecastLocation.TAG, e.getMessage());
        	}
            catch (Exception e)
            {
            	Receiver.SetMessage(e.toString());
        		// Log.e(TAG, e.toString());
            }

            return location;
        }
        
        private void ReadJSON(ForecastLocation location, String json)
        {
        	try
        	{
        		JSONObject jToken = new JSONObject(json);
        		
        		if(jToken.has("location"))
        		{
        			JSONObject loc = jToken.getJSONObject("location");
        			
        			location.City = loc.getString("city");
        			location.State = loc.getString("state");
        			location.Country = loc.getString("country");
        			location.ZipCode = loc.getString("zipCode");
        		}
        	}
        	catch(JSONException e)
        	{
        		Receiver.SetMessage(e.toString());
        		//Log.e(ForecastLocation.TAG, e.getMessage());
        	}
        }

        protected void onPostExecute(ForecastLocation location)
        {
            this.listener.onLocationLoaded(location);
        }
    }
}
